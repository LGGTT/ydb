package com.lggtt.srb.core.service;

import com.lggtt.srb.core.pojo.entity.BorrowInfo;
import com.lggtt.srb.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lggtt.srb.core.pojo.entity.LendItemReturn;
import com.lggtt.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface LendService extends IService<Lend> {

    // 创建标的信息
    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    // 获取标的列表
    List<Lend> selectList();

    // 标的详情
    Map<String, Object> getLendDetail(Long id);

    // 计算利息
    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod);

    // 放款
    void makeLoan(Long id);

    // 回款计划
    List<LendItemReturn> returnInvest(Long lendItemId, Map<Integer, Long> lendReturnMap, Lend lend);

    // 根据用户id获取借款人信息
    List<Lend> selectByUserId(Long userId);
}
