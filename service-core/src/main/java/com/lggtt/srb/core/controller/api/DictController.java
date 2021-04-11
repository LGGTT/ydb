package com.lggtt.srb.core.controller.api;


import com.lggtt.srb.core.pojo.entity.Dict;
import com.lggtt.srb.core.service.DictService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "数据字典")
@CrossOrigin
@RestController
@RequestMapping("/api/core/dict")
public class DictController {

    @Resource
    DictService dictService;

    @ApiOperation("根据 dict_code 查询下级列表")
    @GetMapping("/findByDictCode/{dictCode}")
    public R findByDictCode(@ApiParam(value = "dict_code 参数",required = true)
                            @PathVariable("dictCode")String dictCode){

        List<Dict> list = dictService.findByDictCode(dictCode);

        return R.ok().data("list",list);
    }
}

