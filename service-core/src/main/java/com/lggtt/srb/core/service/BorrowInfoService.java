package com.lggtt.srb.core.service;

import com.lggtt.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lggtt.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    // 获取借款人最大申请额度
    BigDecimal getBorrowAmount(Long userId);

    // 借款申请提交
    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    // 获取借款人状态
    Integer getStatusByUserId(Long userId);

    // 获取借款人列表
    List<BorrowInfo> selectList();

    // 获取借款详情
    Map<String, Object> getBorrowInfoDetail(Long id);

    // 审批借款信息
    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
