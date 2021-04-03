package com.lggtt.srb.core.service.impl;

import com.lggtt.srb.core.entity.UserAccount;
import com.lggtt.srb.core.mapper.UserAccountMapper;
import com.lggtt.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

}