package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lggtt.srb.core.enums.BorrowInfoStatusEnum;
import com.lggtt.srb.core.enums.BorrowerStatusEnum;
import com.lggtt.srb.core.enums.UserBindEnum;
import com.lggtt.srb.core.mapper.BorrowInfoMapper;
import com.lggtt.srb.core.mapper.BorrowerMapper;
import com.lggtt.srb.core.mapper.IntegralGradeMapper;
import com.lggtt.srb.core.mapper.UserInfoMapper;
import com.lggtt.srb.core.pojo.entity.BorrowInfo;
import com.lggtt.srb.core.pojo.entity.Borrower;
import com.lggtt.srb.core.pojo.entity.IntegralGrade;
import com.lggtt.srb.core.pojo.entity.UserInfo;
import com.lggtt.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.lggtt.srb.core.pojo.vo.BorrowerDetailVO;
import com.lggtt.srb.core.service.BorrowInfoService;
import com.lggtt.srb.core.service.BorrowerService;
import com.lggtt.srb.core.service.DictService;
import com.lggtt.srb.core.service.LendService;
import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.ResponseEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    IntegralGradeMapper integralGradeMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    DictService dictService;

    @Resource
    BorrowerMapper borrowerMapper;

    @Resource
    BorrowerService borrowerService;

    @Resource
    LendService lendService;

    @Override
    public BigDecimal getBorrowAmount(Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer integral = userInfo.getIntegral();

        QueryWrapper<IntegralGrade> wrapper = new QueryWrapper<>();
        wrapper.le("integral_start", integral)
                .ge("integral_end", integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(wrapper);
        return integralGrade.getBorrowAmount();
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        // 判断用户状态
        Assert.isTrue(userInfo.getBindStatus().intValue() == UserBindEnum.BIND_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_BIND_ERROR);
        Assert.isTrue(userInfo.getBorrowAuthStatus().intValue() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_AMOUNT_ERROR);

        // 判断借款人额度是否合法
        BigDecimal borrowAmount = getBorrowAmount(userId);
        Assert.isTrue(borrowInfo.getAmount().doubleValue() <= borrowAmount.doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR);

        borrowInfo.setUserId(userId);
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> wrapper = new QueryWrapper<>();
        wrapper.select("status").eq("user_id", userId);

        List<Object> objects = baseMapper.selectObjs(wrapper);
        if (objects.size() == 0) {
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        return (Integer) objects.get(0);

    }

    @Override
    public List<BorrowInfo> selectList() {
        List<BorrowInfo> borrowInfos = baseMapper.selectBorrowInfoList();
        borrowInfos.forEach(this::assemble);
        return borrowInfos;

    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {

        // 借款信息
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        assemble(borrowInfo);

        // 借款人信息
        QueryWrapper<Borrower> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(wrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());


        Map<String,Object> map = new HashMap<>();
        map.put("borrowInfo",borrowInfo);
        map.put("borrower",borrowerDetailVO);

        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoApprovalVO.getId());
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);

        // 审核通过，产生新标的
        if (borrowInfo.getStatus() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue()){
            lendService.createLend(borrowInfoApprovalVO,borrowInfo);
        }
    }


    // 组装 借款信息
    private BorrowInfo assemble(BorrowInfo borrowInfo) {
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());

        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);

        return borrowInfo;
    }

}
