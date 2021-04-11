package com.lggtt.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lggtt.srb.core.pojo.entity.BorrowerAttach;
import com.lggtt.srb.core.mapper.BorrowerAttachMapper;
import com.lggtt.srb.core.pojo.vo.BorrowerAttachVO;
import com.lggtt.srb.core.pojo.vo.BorrowerDetailVO;
import com.lggtt.srb.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVO> getBorrowerAttachVOList(Long borrowerId) {

        QueryWrapper<BorrowerAttach> wrapper = new QueryWrapper<>();
        wrapper.eq("borrower_id",borrowerId);

        List<BorrowerAttach> borrowerAttaches = baseMapper.selectList(wrapper);
        List<BorrowerAttachVO> list = new ArrayList<>();
        borrowerAttaches.forEach(borrowerAttache ->{
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            BeanUtils.copyProperties(borrowerAttache,borrowerAttachVO);
            list.add(borrowerAttachVO);
        });

        return list;
    }
}
