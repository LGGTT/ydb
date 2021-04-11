package com.lggtt.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lggtt.srb.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lggtt.srb.core.pojo.vo.BorrowerApprovalVO;
import com.lggtt.srb.core.pojo.vo.BorrowerDetailVO;
import com.lggtt.srb.core.pojo.vo.BorrowerVO;

import java.util.List;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface BorrowerService extends IService<Borrower> {

    // 保存借款人信息
    void saveBorrowByUserId(Long userId, BorrowerVO borrowerVO);

    // 获取借款人状态
    Integer getStatusByUserId(Long userId);

    // 获取借款人列表
    IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword);

    // 获取借款人详情
    BorrowerDetailVO getBorrowerDetailVOById(Long id);

    // 借款人信息审核
    void approval(BorrowerApprovalVO borrowerApprovalVO);
}
