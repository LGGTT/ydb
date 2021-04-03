package com.lggtt.srb.core.service.impl;

import com.lggtt.srb.core.entity.UserLoginRecord;
import com.lggtt.srb.core.mapper.UserLoginRecordMapper;
import com.lggtt.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

}
