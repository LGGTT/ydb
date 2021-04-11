package com.lggtt.srb.core.mapper;

import com.lggtt.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lggtt.srb.core.pojo.dto.ExcelDictDto;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertList(List<ExcelDictDto> list);
}
