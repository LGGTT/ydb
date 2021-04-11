package com.lggtt.srb.core.controller.admin;


import com.lggtt.srb.core.pojo.entity.IntegralGrade;
import com.lggtt.srb.core.service.IntegralGradeService;
import com.lggtt.srb.exception.Assert;
import com.lggtt.srb.result.R;
import com.lggtt.srb.result.ResponseEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "积分等级管理")
@CrossOrigin
@RestController
@RequestMapping("admin/core/integralGrade")
public class AdminIntegralGradeController {
    @Resource
    IntegralGradeService integralGradeService;

    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public R listAll() {
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list", list);
    }

    @ApiOperation("根据id删除积分等级记录")
    @DeleteMapping("/remove/{id}")
    public R removeById(
            @ApiParam(value = "积分等级id", example = "1", required = true)
            @PathVariable("id") Integer id) {
        boolean is_deleted = integralGradeService.removeById(id);
        if (is_deleted) {
            return R.ok().message("数据删除成功");
        } else
            return R.error().message("数据删除失败");
    }

    @ApiOperation("根据某个id查找积分等级")
    @GetMapping("/get/{id}")
    public R getById(
            @ApiParam(value = "积分等级id", example = "1", required = true)
            @PathVariable("id") Integer id) {
        IntegralGrade res = integralGradeService.getById(id);
        if (res == null)
            return R.error().message("未找到这条数据");
        else
            return R.ok().data("res", res);
    }

    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade) {
        Assert.notNull(integralGrade.getBorrowAmount(), ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        boolean saved = integralGradeService.save(integralGrade);
        if (saved)
            return R.ok().message("新增成功");
        else
            return R.error().message("新增失败");
    }

    @ApiOperation("修改积分等级")
    @PutMapping("/update")
    public R update(
            @ApiParam(value = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade){
        boolean updated = integralGradeService.updateById(integralGrade);
        if (updated)
            return R.ok().message("更新成功");
        else
            return R.error().message("更新失败");
    }

}

