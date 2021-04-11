package com.lggtt.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lggtt.srb.core.pojo.entity.UserInfo;
import com.lggtt.srb.core.pojo.query.UserInfoQuery;
import com.lggtt.srb.core.service.UserInfoService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "用户管理")
@CrossOrigin
@RestController
@RequestMapping("/admin/core/userInfo")
public class AdminUserInfoController {

    @Resource
    UserInfoService userInfoService;

    @ApiOperation("用户分页列表")
    @GetMapping("/listPage/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页数", required = true)
                      @PathVariable("page") Long page,

                      @ApiParam(value = "每页记录数", required = true)
                      @PathVariable("limit") Long limit,

                      @ApiParam(value = "查询条件")
                              UserInfoQuery userInfoQuery) {
        Page<UserInfo> userInfos = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.listPage(userInfos, userInfoQuery);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation("锁定用户")
    @PutMapping("/lock/{id}/{status}")
    public R locke(@ApiParam(value = "用户id", required = true)
                   @PathVariable("id") Long id,

                   @ApiParam(value = "锁定状态（0：锁定 1：正常）", required = true)
                   @PathVariable("status") Integer status) {

        userInfoService.lock(id, status);
        return R.ok().message(status == 0 ? "锁定成功" : "解锁成功");
    }
}

