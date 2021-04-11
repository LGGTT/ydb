package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lggtt.srb.core.mapper.LendItemMapper;
import com.lggtt.srb.core.mapper.LendMapper;
import com.lggtt.srb.core.mapper.LendReturnMapper;
import com.lggtt.srb.core.pojo.entity.Lend;
import com.lggtt.srb.core.pojo.entity.LendItem;
import com.lggtt.srb.core.pojo.entity.LendItemReturn;
import com.lggtt.srb.core.mapper.LendItemReturnMapper;
import com.lggtt.srb.core.pojo.entity.LendReturn;
import com.lggtt.srb.core.service.LendItemReturnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lggtt.srb.core.service.LendItemService;
import com.lggtt.srb.core.service.LendReturnService;
import com.lggtt.srb.core.service.UserBindService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {

    @Resource
    LendReturnMapper lendReturnMapper;

    @Resource
    LendMapper lendMapper;

    @Resource
    LendItemMapper lendItemMapper;

    @Resource
    UserBindService userBindService;

    @Override
    public List<LendItemReturn> selectByLendId(Long lendId, Long userId) {
        QueryWrapper<LendItemReturn> wrapper = new QueryWrapper<>();
        if (lendId == null){
            wrapper.eq("invest_user_id",userId).orderByDesc("current_period");
            return baseMapper.selectList(wrapper);
        }
        wrapper.eq("lend_id",lendId)
                .eq("invest_user_id",userId)
                .orderByAsc("current_period");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> addReturnDetail(Long lendReturnId) {
        //获取还款记录
        LendReturn lendReturn = lendReturnMapper.selectById(lendReturnId);
        //获取标的信息
        Lend lend = lendMapper.selectById(lendReturn.getLendId());

        //根据还款id获取回款列表
        List<LendItemReturn> lendItemReturnList = this.selectLendItemReturnList(lendReturnId);
        List<Map<String, Object>> lendItemReturnDetailList = new ArrayList<>();
        for(LendItemReturn lendItemReturn : lendItemReturnList) {
            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
            String bindCode = userBindService.getBindCodeByUserId(lendItem.getInvestUserId());

            Map<String, Object> map = new HashMap<>();
            //项目编号
            map.put("agentProjectCode", lend.getLendNo());
            //出借编号
            map.put("voteBillNo", lendItem.getLendItemNo());
            //收款人（出借人）
            map.put("toBindCode", bindCode);
            //还款金额
            map.put("transitAmt", lendItemReturn.getTotal());
            //还款本金
            map.put("baseAmt", lendItemReturn.getPrincipal());
            //还款利息
            map.put("benifitAmt", lendItemReturn.getInterest());
            //商户手续费
            map.put("feeAmt", new BigDecimal("0"));

            lendItemReturnDetailList.add(map);
        }
        return lendItemReturnDetailList;
    }

    @Override
    public List<LendItemReturn> selectLendItemReturnList(Long lendReturnId) {
        QueryWrapper<LendItemReturn> wrapper = new QueryWrapper<>();
        wrapper.eq("lend_return_id",lendReturnId);

        return baseMapper.selectList(wrapper);
    }
}
