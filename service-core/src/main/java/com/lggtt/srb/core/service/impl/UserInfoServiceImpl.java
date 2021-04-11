package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lggtt.srb.base.util.JwtUtils;
import com.lggtt.srb.core.mapper.UserAccountMapper;
import com.lggtt.srb.core.mapper.UserInfoMapper;
import com.lggtt.srb.core.mapper.UserLoginRecordMapper;
import com.lggtt.srb.core.pojo.entity.UserAccount;
import com.lggtt.srb.core.pojo.entity.UserInfo;
import com.lggtt.srb.core.pojo.entity.UserLoginRecord;
import com.lggtt.srb.core.pojo.query.UserInfoQuery;
import com.lggtt.srb.core.pojo.vo.LoginVO;
import com.lggtt.srb.core.pojo.vo.RegisterVO;
import com.lggtt.srb.core.pojo.vo.UserIndexVO;
import com.lggtt.srb.core.pojo.vo.UserInfoVO;
import com.lggtt.srb.core.service.UserInfoService;
import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.ResponseEnum;
import com.lggtt.srb.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    UserAccountMapper userAccountMapper;

    @Resource
    UserLoginRecordMapper userLoginRecordMapper;

    // 用户注册
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {

        // 验证手机号是否已注册
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", registerVO.getMobile());
        Assert.isTrue(0 == baseMapper.selectCount(wrapper), ResponseEnum.MOBILE_EXIST_ERROR);

        // user_info 表中插入数据
        UserInfo userInfo = new UserInfo();
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setHeadImg(UserInfo.DEFAULT_AVATAR_URL);
        baseMapper.insert(userInfo);

        // user_account 表插入数据
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }

    // 用户登录
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {

        Integer userType = loginVO.getUserType();
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        // 用户是否存在
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile)
                .eq("user_type", userType);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        // 密码是否正确
        Assert.equals(userInfo.getPassword(), MD5.encrypt(password), ResponseEnum.LOGIN_PASSWORD_ERROR);

        // 用户是否被禁用
        Assert.equals(userInfo.getStatus(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);

        // 记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setIp(ip);
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecordMapper.insert(userLoginRecord);

        // 生成 token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());

        // 组装 UserInfoVo
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserType(userInfo.getUserType());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        userInfoVO.setToken(token);
        userInfoVO.setMobile(userInfo.getMobile());
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setNickName(userInfo.getNickName());

        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> userInfos, UserInfoQuery userInfoQuery) {

        // 判断查询条件是否为空
        if (userInfoQuery == null) {
            return baseMapper.selectPage(userInfos, null);
        }


        String mobile = userInfoQuery.getMobile();
        Integer status = userInfoQuery.getStatus();
        Integer userType = userInfoQuery.getUserType();

        // 组装查询条件
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(mobile), "mobile", mobile)
                .eq(status != null,"status", status)
                .eq(userType!= null,"user_type", userType);

            return baseMapper.selectPage(userInfos,wrapper);

    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);

        baseMapper.updateById(userInfo);

    }

    @Override
    public UserInfo checkMobile(String mobile) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);

        UserInfo userInfo = new UserInfo();
        userInfo.setMobile(mobile);

        return baseMapper.selectOne(wrapper);
    }

    @Override
    public UserIndexVO getIndexUserInfo(Long userId) {
        //用户信息
        UserInfo userInfo = baseMapper.selectById(userId);

        //账户信息
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);

        //登录信息
        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper
                .eq("user_id", userId)
                .orderByDesc("id")
                .last("limit 1");
        UserLoginRecord userLoginRecord = userLoginRecordMapper.selectOne(userLoginRecordQueryWrapper);

        //组装结果数据
        UserIndexVO userIndexVO = new UserIndexVO();
        userIndexVO.setUserId(userInfo.getId());
        userIndexVO.setUserType(userInfo.getUserType());
        userIndexVO.setName(userInfo.getName());
        userIndexVO.setNickName(userInfo.getNickName());
        userIndexVO.setHeadImg(userInfo.getHeadImg());
        userIndexVO.setBindStatus(userInfo.getBindStatus());
        userIndexVO.setAmount(userAccount.getAmount());
        userIndexVO.setFreezeAmount(userAccount.getFreezeAmount());
        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());

        return userIndexVO;

    }
}
