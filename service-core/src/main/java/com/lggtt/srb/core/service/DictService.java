package com.lggtt.srb.core.service;

import com.lggtt.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lggtt.srb.core.pojo.dto.ExcelDictDto;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
public interface DictService extends IService<Dict> {
    void importData(InputStream inputStream);

    List<ExcelDictDto> listDictData();

    List<Dict> listByParentId(Long id);

    List<Dict> findByDictCode(String dictCode);

    String getNameByParentDictCodeAndValue(String dictCode,Integer value);
}
