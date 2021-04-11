package com.lggtt.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lggtt.srb.core.pojo.bo.TransFlowBO;
import com.lggtt.srb.core.pojo.entity.TransFlow;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface TransFlowService extends IService<TransFlow> {

    // 保存流水号
    void saveTransFlow(TransFlowBO transFlowBo);

    // 判断流水号是否存在
    Boolean isSaveTransFlow(String agentBillNo);

    // 获取交易流水列表
    List<TransFlow> selectByUserId(Long userId);
}
