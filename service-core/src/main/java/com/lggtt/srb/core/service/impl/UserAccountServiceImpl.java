package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lggtt.srb.core.enums.TransTypeEnum;
import com.lggtt.srb.core.hfb.FormHelper;
import com.lggtt.srb.core.hfb.HfbConst;
import com.lggtt.srb.core.hfb.RequestHelper;
import com.lggtt.srb.core.mapper.TransFlowMapper;
import com.lggtt.srb.core.mapper.UserInfoMapper;
import com.lggtt.srb.core.pojo.bo.TransFlowBO;
import com.lggtt.srb.core.pojo.entity.TransFlow;
import com.lggtt.srb.core.pojo.entity.UserAccount;
import com.lggtt.srb.core.mapper.UserAccountMapper;
import com.lggtt.srb.core.pojo.entity.UserInfo;
import com.lggtt.srb.core.service.TransFlowService;
import com.lggtt.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lggtt.srb.core.service.UserBindService;
import com.lggtt.srb.core.util.LendNoUtils;
import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    UserAccountMapper userAccountMapper;

    @Resource
    TransFlowService transFlowService;

    @Resource
    UserBindService userBindService;

    @Resource
    UserAccountService userAccountService;

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        //获取充值人绑定协议号
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getChargeNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));


        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;


    }

    @Override
    public String callback(Map<String, Object> paramMap) {

        // 幂等性判断 判断交易流水是否存在
        String agentBillNo = (String)paramMap.get("agentBillNo");
        Boolean saveTransFlow = transFlowService.isSaveTransFlow(agentBillNo);
        if (saveTransFlow){
            log.debug("幂等性返回");
            return "success";
        }

        // 账户处理
        String bindCode = (String) paramMap.get("bindCode");
        String chargeAmt = (String) paramMap.get("chargeAmt");
        userAccountMapper.updateAccount(bindCode,new BigDecimal(chargeAmt),new BigDecimal(0));

        // 记录账户流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);

        return "success";
    }

    @Override
    public BigDecimal getAccount(Long userId) {
        QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        UserAccount userAccount = baseMapper.selectOne(wrapper);
        return  userAccount.getAmount();
    }

    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {

        BigDecimal account = userAccountService.getAccount(userId);
        Assert.isTrue(account.doubleValue() >= fetchAmt.doubleValue(),
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        String bindCode = userBindService.getBindCodeByUserId(userId);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("feeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
        return formStr;
    }

    @Override
    public void notifyWithdraw(Map<String, Object> paramMap) {

        // 幂等性判断
        String agentBillNo = (String)paramMap.get("agentBillNo");
        if (transFlowService.isSaveTransFlow(agentBillNo)){
            log.debug("幂等性返回");
            return;
        }
        // 账户同步
        String bindCode = (String)paramMap.get("bindCode");
        String fetchAmt = (String)paramMap.get("fetchAmt");
        baseMapper.updateAccount(bindCode, new BigDecimal("-" + fetchAmt), new BigDecimal(0));

        //增加交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(fetchAmt),
                TransTypeEnum.WITHDRAW,
                "提现");
        transFlowService.saveTransFlow(transFlowBO);
    }
}
