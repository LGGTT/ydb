package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lggtt.srb.core.enums.BorrowerStatusEnum;
import com.lggtt.srb.core.enums.IntegralEnum;
import com.lggtt.srb.core.mapper.BorrowerAttachMapper;
import com.lggtt.srb.core.mapper.BorrowerMapper;
import com.lggtt.srb.core.mapper.UserInfoMapper;
import com.lggtt.srb.core.mapper.UserIntegralMapper;
import com.lggtt.srb.core.pojo.entity.Borrower;
import com.lggtt.srb.core.pojo.entity.BorrowerAttach;
import com.lggtt.srb.core.pojo.entity.UserInfo;
import com.lggtt.srb.core.pojo.entity.UserIntegral;
import com.lggtt.srb.core.pojo.vo.BorrowerApprovalVO;
import com.lggtt.srb.core.pojo.vo.BorrowerAttachVO;
import com.lggtt.srb.core.pojo.vo.BorrowerDetailVO;
import com.lggtt.srb.core.pojo.vo.BorrowerVO;
import com.lggtt.srb.core.service.BorrowerAttachService;
import com.lggtt.srb.core.service.BorrowerService;
import com.lggtt.srb.core.service.DictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    DictService dictService;

    @Resource
    BorrowerAttachService borrowerAttachService;

    @Resource
    UserIntegralMapper userIntegralMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowByUserId(Long userId, BorrowerVO borrowerVO) {
        UserInfo userInfo = userInfoMapper.selectById(userId);

        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);

        // 附件保存
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(item -> {
            item.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(item);
        });

        // 更新用户状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);

    }

    @Override
    public Integer getStatusByUserId(Long userId) {

        QueryWrapper<Borrower> wrapper = new QueryWrapper<>();
        wrapper.select("status").eq("user_id", userId);

        Borrower borrower = baseMapper.selectOne(wrapper);
        if (borrower == null) {
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        return borrower.getStatus();
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return baseMapper.selectPage(pageParam, null);
        }

        QueryWrapper<Borrower> wrapper = new QueryWrapper<>();
        wrapper.like("name", keyword)
                .or().like("mobile", keyword)
                .or().like("id_card", keyword)
                .orderByDesc("id");

        return baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {

        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        Borrower borrower = baseMapper.selectById(id);
        BeanUtils.copyProperties(borrower, borrowerDetailVO);

        borrowerDetailVO.setMarry(borrower.getMarry() ? "是" : "否");
        borrowerDetailVO.setSex(borrower.getSex() == 1 ? "男" : "女");
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);


        //下拉列表
        borrowerDetailVO.setEducation(dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation()));
        borrowerDetailVO.setIndustry(dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry()));
        borrowerDetailVO.setIncome(dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome()));
        borrowerDetailVO.setReturnSource(dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource()));
        borrowerDetailVO.setContactsRelation(dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation()));

        // 附件列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.getBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return borrowerDetailVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        Long borrowerId = borrowerApprovalVO.getBorrowerId();


        // 更新借款人状态
        Borrower borrower = baseMapper.selectById(borrowerId);
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        // 获取用户信息
        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer integral = userInfo.getIntegral();

        UserIntegral userIntegral1 = new UserIntegral();
        userIntegral1.setUserId(userId);
        userIntegral1.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral1.setContent("基本信息初始积分");
        userIntegralMapper.insert(userIntegral1);
        integral += borrowerApprovalVO.getInfoIntegral();


        // car ok
        if (borrowerApprovalVO.getIsCarOk()) {
            UserIntegral userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            integral += IntegralEnum.BORROWER_CAR.getIntegral();
        }

        // house ok
        if (borrowerApprovalVO.getIsHouseOk()) {
            UserIntegral userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            integral += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        // id card ok
        if (borrowerApprovalVO.getIsIdCardOk()) {
            UserIntegral userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            integral += IntegralEnum.BORROWER_IDCARD.getIntegral();
        }

        // 更新用户状态
        userInfo.setIntegral(integral);
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(userInfo);

    }
}
