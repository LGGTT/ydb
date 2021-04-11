package com.lggtt.srb.core.service;

import com.lggtt.srb.core.pojo.entity.UserLoginRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    // 根据用户 id 查询登录日志
    List<UserLoginRecord> listTop50(Long userId);
}
