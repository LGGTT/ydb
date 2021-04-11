package com.lggtt.srb.core.service;

import com.lggtt.srb.core.pojo.entity.LendItemReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface LendItemReturnService extends IService<LendItemReturn> {

    // 获取回款列表
    List<LendItemReturn> selectByLendId(Long lendId, Long userId);

    //
    List<Map<String, Object>> addReturnDetail(Long lendReturnId);

    // 根据还款记录id查询对应的回款记录
    List<LendItemReturn> selectLendItemReturnList(Long lendReturnId);
}
