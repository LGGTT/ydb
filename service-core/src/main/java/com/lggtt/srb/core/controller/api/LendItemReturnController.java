package com.lggtt.srb.core.controller.api;


import com.lggtt.srb.base.util.JwtUtils;
import com.lggtt.srb.core.pojo.entity.LendItemReturn;
import com.lggtt.srb.core.service.LendItemReturnService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "回款计划")
@CrossOrigin
@RestController
@RequestMapping("/api/core/lendItemReturn")
public class LendItemReturnController {

    @Resource
    private LendItemReturnService lendItemReturnService;

    @ApiOperation("获取列表")
    @GetMapping("/auth/list/{lendId}")
    public R list(
            @ApiParam(value = "标的id")
            @PathVariable Long lendId, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<LendItemReturn> list = lendItemReturnService.selectByLendId(lendId, userId);
        return R.ok().data("list", list);
    }

    @ApiOperation("获取列表")
    @GetMapping("/auth/listByUserId")
    public R listByUserId(HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<LendItemReturn> list = lendItemReturnService.selectByLendId(null, userId);
        return R.ok().data("list", list);
    }
}

