package com.lggtt.srb.core.service;

import com.lggtt.srb.core.pojo.entity.LendReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface LendReturnService extends IService<LendReturn> {

    // 查询所有还款计划
    List<LendReturn> selectByLendId(Long lendId);

    // 借款人还款
    String commitReturn(Long lendReturnId, Long userId);

    // 还款异步回调
    void notifyUrl(Map<String, Object> paramMap);

    // 根据用户查询还款计划
    List<LendReturn> selectByUserId(Long userId);
}
