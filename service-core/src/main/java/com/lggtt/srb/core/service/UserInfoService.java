package com.lggtt.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lggtt.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lggtt.srb.core.pojo.query.UserInfoQuery;
import com.lggtt.srb.core.pojo.vo.LoginVO;
import com.lggtt.srb.core.pojo.vo.RegisterVO;
import com.lggtt.srb.core.pojo.vo.UserIndexVO;
import com.lggtt.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface UserInfoService extends IService<UserInfo> {

    // 用户注册
    void register(RegisterVO registerVO);

    // 用户登录
    UserInfoVO login(LoginVO loginVO, String ip);

    // 分页列表查询
    IPage<UserInfo> listPage(Page<UserInfo> userInfos, UserInfoQuery userInfoQuery);

    // 锁定用户
    void lock(Long id, Integer status);

    // 检查手机号是否注册
    UserInfo checkMobile(String mobile);

    UserIndexVO getIndexUserInfo(Long userId);
}
