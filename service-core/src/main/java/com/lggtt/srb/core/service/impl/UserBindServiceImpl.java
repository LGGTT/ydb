package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lggtt.srb.core.enums.UserBindEnum;
import com.lggtt.srb.core.hfb.FormHelper;
import com.lggtt.srb.core.hfb.HfbConst;
import com.lggtt.srb.core.hfb.RequestHelper;
import com.lggtt.srb.core.mapper.UserInfoMapper;
import com.lggtt.srb.core.pojo.entity.UserBind;
import com.lggtt.srb.core.mapper.UserBindMapper;
import com.lggtt.srb.core.pojo.entity.UserInfo;
import com.lggtt.srb.core.pojo.vo.UserBindVO;
import com.lggtt.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.ResponseEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {

        // 不允许身份证号相同，用户不同的情况
        QueryWrapper<UserBind> wrapper = new QueryWrapper<>();
        wrapper.eq("id_card",userBindVO.getIdCard())
                .ne("user_id",userId);
        UserBind userBind = baseMapper.selectOne(wrapper);
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        // 查询用户是否填写过表单
        wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        userBind = baseMapper.selectOne(wrapper);

        if (userBind == null){
            // 没填写，插入新的记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO,userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        }else {
            // 填写过，更新记录
            BeanUtils.copyProperties(userBindVO,userBind);
            baseMapper.updateById(userBind);
        }


        //组装自动提交表单的参数
        Map<String, Object> params = new HashMap<>();
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentUserId", userId);
        params.put("idCard",userBindVO.getIdCard());
        params.put("personalName", userBindVO.getName());
        params.put("bankType", userBindVO.getBankType());
        params.put("bankNo", userBindVO.getBankNo());
        params.put("mobile", userBindVO.getMobile());
        params.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        params.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        params.put("timestamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));

        //生成动态表单字符串
        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL, params);
        return formStr;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void callback(Map<String, Object> params) {

        String bindCode = (String)params.get("bindCode");
        String agentUserId = (String)params.get("agentUserId");

        //根据user_id查询user_bind记录
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", agentUserId);

        //更新用户绑定表
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        //更新用户表
        UserInfo userInfo = userInfoMapper.selectById(agentUserId);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);

    }

    @Override
    public String getBindCodeByUserId(Long userId) {

        QueryWrapper<UserBind> wrapper = new QueryWrapper<>();
        wrapper.select("bind_code").eq("user_id",userId);
        List<Object> objects = baseMapper.selectObjs(wrapper);
        return (String)objects.get(0);
    }


}
