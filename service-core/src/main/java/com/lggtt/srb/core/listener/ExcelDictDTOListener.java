package com.lggtt.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lggtt.srb.core.mapper.DictMapper;
import com.lggtt.srb.core.pojo.dto.ExcelDictDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDto> {

    private List<ExcelDictDto> list = new ArrayList<>();
    // 每次往数据库插入5条数据
    private static final int BATCH_COUNT = 5;

    private DictMapper dictMapper;

    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDto excelDictDto, AnalysisContext analysisContext) {
        list.add(excelDictDto);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }

    private void saveData() {
        dictMapper.insertList(list);
        log.debug("{}条数据被插入到数据库",list.size());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 当剩余数据不到5条时，会把剩余的数据插入到数据库
        saveData();
        log.debug("导入完成");
    }
}
