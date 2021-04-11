package com.lggtt.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lggtt.srb.core.listener.ExcelDictDTOListener;
import com.lggtt.srb.core.mapper.DictMapper;
import com.lggtt.srb.core.pojo.dto.ExcelDictDto;
import com.lggtt.srb.core.pojo.entity.Dict;
import com.lggtt.srb.core.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    RedisTemplate redisTemplate;

    // excel 导入服务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDto.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
    }

    // excel 导出服务
    @Override
    public List<ExcelDictDto> listDictData() {
        List<Dict> dicts = baseMapper.selectList(null);
        ArrayList<ExcelDictDto> dtos = new ArrayList<>();
        dicts.forEach(item -> {
            ExcelDictDto dto = new ExcelDictDto();
            BeanUtils.copyProperties(item, dto);
            dtos.add(dto);
        });
        return dtos;
    }


    @Override
    public List<Dict> listByParentId(Long parentId) {

//        try {
//            List<Dict> list = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
//            if (list != null) {
//                log.debug("从 redis 中获取到 parentId 为 {} 的字典数据",parentId);
//                return list;
//            }
//        }catch (Exception e){
//            log.error("redis 服务器获取数据异常：{}", ExceptionUtils.getStackTrace(e));
//        }
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<Dict> dicts = baseMapper.selectList(wrapper);
        dicts.forEach(dict -> {
            dict.setHasChildren(hasChildren(dict));
        });
//        try{
//            redisTemplate.opsForValue().set("srb:core:dictList:"+parentId,dicts,5, TimeUnit.MINUTES);
//            log.debug(" parentId 为 {} 的字典数据存入 redis 中",parentId);
//        }catch (Exception e){
//            log.error("redis 存入数据异常：{}",ExceptionUtils.getStackTrace(e));
//        }

        return dicts;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(wrapper);

        return listByParentId(dict.getId());
    }

    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {

        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict parentDict = baseMapper.selectOne(wrapper);
        if (parentDict == null){
            return "";
        }

        wrapper.clear();
        wrapper.eq("parent_id",parentDict.getId())
                .eq("value",value);

        Dict dict = baseMapper.selectOne(wrapper);
        if(dict == null){
            return "";
        }
        return dict.getName() ;

    }

    private Boolean hasChildren(Dict dict) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", dict.getId());
        Integer integer = baseMapper.selectCount(wrapper);
        if (integer > 0)
            return true;
        return false;
    }


}
