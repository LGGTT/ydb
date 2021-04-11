package com.lggtt.srb.core.controller.admin;


import com.lggtt.srb.core.pojo.entity.UserLoginRecord;
import com.lggtt.srb.core.service.UserLoginRecordService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "用户登录日志")
@CrossOrigin
@RestController
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {

    @Resource
    UserLoginRecordService recordService;

    @ApiOperation("获取登录日志")
    @GetMapping("/listTop50/{id}")
    public R listTop50(@ApiParam(value = "用户 id",required = true)
                       @PathVariable("id")Long id){
        List<UserLoginRecord> loginRecords =  recordService.listTop50(id);
        return R.ok().data("list",loginRecords);
    }
}

