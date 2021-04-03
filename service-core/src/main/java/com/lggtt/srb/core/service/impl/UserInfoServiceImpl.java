package com.lggtt.srb.core.service.impl;

import com.lggtt.srb.core.entity.UserInfo;
import com.lggtt.srb.core.mapper.UserInfoMapper;
import com.lggtt.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
