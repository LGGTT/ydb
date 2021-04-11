package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lggtt.srb.core.enums.LendStatusEnum;
import com.lggtt.srb.core.enums.TransTypeEnum;
import com.lggtt.srb.core.hfb.FormHelper;
import com.lggtt.srb.core.hfb.HfbConst;
import com.lggtt.srb.core.hfb.RequestHelper;
import com.lggtt.srb.core.mapper.LendItemMapper;
import com.lggtt.srb.core.mapper.LendMapper;
import com.lggtt.srb.core.mapper.UserAccountMapper;
import com.lggtt.srb.core.pojo.bo.TransFlowBO;
import com.lggtt.srb.core.pojo.entity.Lend;
import com.lggtt.srb.core.pojo.entity.LendItem;
import com.lggtt.srb.core.pojo.vo.InvestVO;
import com.lggtt.srb.core.service.*;
import com.lggtt.srb.core.util.LendNoUtils;
import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.ResponseEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    @Resource
    LendMapper lendMapper;

    @Resource
    UserAccountService userAccountService;

    @Resource
    UserBindService userBindService;

    @Resource
    LendService lendService;

    @Resource
    TransFlowService transFlowService;

    @Resource
    UserAccountMapper userAccountMapper;

    @Override
    public String commitInvest(InvestVO investVO) {

        Long lendId = investVO.getLendId();
        Long investUserId = investVO.getInvestUserId();
        Lend lend = lendMapper.selectById(lendId);

        // 校验标的状态
        Assert.isTrue(lend.getStatus() == LendStatusEnum.INVEST_RUN.getStatus(),
                ResponseEnum.LEND_INVEST_ERROR);

        // 超出可投金额
        BigDecimal sum = lend.getInvestAmount().add(new BigDecimal(investVO.getInvestAmount()));
        Assert.isTrue(lend.getAmount().doubleValue() >= sum.doubleValue(),
                ResponseEnum.LEND_FULL_SCALE_ERROR);

        // 判断用户余额是否充足
        BigDecimal account = userAccountService.getAccount(investUserId);
        Assert.isTrue(account.doubleValue() >= new BigDecimal(investVO.getInvestAmount()).doubleValue(),
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        // 获取投资人的 bindCode
        String bindCode = userBindService.getBindCodeByUserId(investUserId);

        // 获取借款人的 bindCode
        String benefitBindCode = userBindService.getBindCodeByUserId(lend.getUserId());

        // 记录投资记录
        LendItem lendItem = new LendItem();
        lendItem.setInvestUserId(investUserId);//投资人id
        lendItem.setInvestName(investVO.getInvestName());//投资人名字
        String lendItemNo = LendNoUtils.getLendItemNo();
        lendItem.setLendItemNo(lendItemNo); //投资条目编号（一个Lend对应一个或多个LendItem）
        lendItem.setLendId(investVO.getLendId());//对应的标的id
        lendItem.setInvestAmount(new BigDecimal(investVO.getInvestAmount())); //此笔投资金额
        lendItem.setLendYearRate(lend.getLendYearRate());//年化
        lendItem.setInvestTime(LocalDateTime.now()); //投资时间
        lendItem.setLendStartDate(lend.getLendStartDate()); //开始时间
        lendItem.setLendEndDate(lend.getLendEndDate()); //结束时间

        BigDecimal expectAmount = lendService.getInterestCount(
                lendItem.getInvestAmount(),
                lendItem.getLendYearRate(),
                lend.getPeriod(),
                lend.getReturnMethod());
        lendItem.setExpectAmount(expectAmount);    // 平台预期收益
        lendItem.setRealAmount(new BigDecimal(0)); // 平台实际收益
        lendItem.setStatus(0); //投资记录状态
        baseMapper.insert(lendItem);

        //封装提交至汇付宝的参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("voteBindCode", bindCode);
        paramMap.put("benefitBindCode", benefitBindCode);
        paramMap.put("agentProjectCode", lend.getLendNo());//项目标号
        paramMap.put("agentProjectName", lend.getTitle());

        //在资金托管平台上的投资订单的唯一编号，要和lendItemNo保持一致。
        paramMap.put("agentBillNo", lendItemNo);//订单编号
        paramMap.put("voteAmt", investVO.getInvestAmount());
        paramMap.put("votePrizeAmt", "0");
        paramMap.put("voteFeeAmt", "0");
        paramMap.put("projectAmt", lend.getAmount()); //标的总金额
        paramMap.put("note", "");
        paramMap.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL); //检查常量是否正确
        paramMap.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.INVEST_URL, paramMap);
        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void callback(Map<String, Object> paramMap) {
        //获取投资编号
        String agentBillNo = (String)paramMap.get("agentBillNo");

        boolean result = transFlowService.isSaveTransFlow(agentBillNo);
        if(result){
            log.warn("幂等性返回");
            return;
        }

        //获取用户的绑定协议号
        String bindCode = (String)paramMap.get("voteBindCode");
        String voteAmt = (String)paramMap.get("voteAmt");

        //修改商户系统中的用户账户金额：余额、冻结金额
        userAccountMapper.updateAccount(
                bindCode,
                new BigDecimal("-" + voteAmt),
                new BigDecimal(voteAmt));

        //修改投资记录的投资状态改为已支付
        LendItem lendItem = this.getByLendItemNo(agentBillNo);
        lendItem.setStatus(1);//已支付
        baseMapper.updateById(lendItem);

        //修改标的信息：投资人数、已投金额
        Long lendId = lendItem.getLendId();
        Lend lend = lendMapper.selectById(lendId);
        lend.setInvestNum(lend.getInvestNum() + 1);
        lend.setInvestAmount(lend.getInvestAmount().add(lendItem.getInvestAmount()));
        lendMapper.updateById(lend);

        //新增交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(voteAmt),
                TransTypeEnum.INVEST_LOCK,
                "投资项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle());
        transFlowService.saveTransFlow(transFlowBO);
    }

    @Override
    public List<LendItem> selectByLendId(Long lendId, Integer status) {

        QueryWrapper<LendItem> wrapper = new QueryWrapper<>();
        wrapper.eq("lend_id",lendId)
                .eq("status",status);

        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<LendItem> selectByLendId(Long lendId) {
        QueryWrapper<LendItem> wrapper = new QueryWrapper<>();
        wrapper.eq("lend_id",lendId);
        return baseMapper.selectList(wrapper);

    }

    @Override
    public List<LendItem> selectByInvestUserId(Long investUserId) {
        QueryWrapper<LendItem> wrapper = new QueryWrapper<>();
        wrapper.eq("invest_user_id",investUserId);
        return baseMapper.selectList(wrapper);

    }

    private LendItem getByLendItemNo(String lendItemNo) {
        QueryWrapper<LendItem> queryWrapper = new QueryWrapper();
        queryWrapper.eq("lend_item_no", lendItemNo);
        return baseMapper.selectOne(queryWrapper);
    }
}
