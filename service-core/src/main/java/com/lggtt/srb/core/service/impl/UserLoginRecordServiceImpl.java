package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lggtt.srb.core.pojo.entity.UserLoginRecord;
import com.lggtt.srb.core.mapper.UserLoginRecordMapper;
import com.lggtt.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        QueryWrapper<UserLoginRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId)
                .orderByDesc("id")
                .last("limit 50");
        List<UserLoginRecord> loginRecords = baseMapper.selectList(wrapper);

        return loginRecords;
    }
}
