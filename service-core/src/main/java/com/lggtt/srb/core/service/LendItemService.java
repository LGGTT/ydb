package com.lggtt.srb.core.service;

import com.lggtt.srb.core.pojo.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lggtt.srb.core.pojo.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface LendItemService extends IService<LendItem> {

    // 调用第三方接口，记录投资记录
    String commitInvest(InvestVO investVO);

    // 第三方确认投资后接口回调，修改金额
    void callback(Map<String, Object> paramMap);

    // 获取投资人列表
    List<LendItem> selectByLendId(Long lendId,Integer status);

    // 获取所有投资人列表
    List<LendItem> selectByLendId(Long lendId);

    // 获取投资所投列表
    List<LendItem> selectByInvestUserId(Long investUserId);
}
