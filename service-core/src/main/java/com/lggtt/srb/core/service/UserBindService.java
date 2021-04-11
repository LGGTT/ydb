package com.lggtt.srb.core.service;

import com.lggtt.srb.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lggtt.srb.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface UserBindService extends IService<UserBind> {

    String commitBindUser(UserBindVO userBindVO, Long userId);

    void callback(Map<String, Object> params);

    String getBindCodeByUserId(Long userId);
}
