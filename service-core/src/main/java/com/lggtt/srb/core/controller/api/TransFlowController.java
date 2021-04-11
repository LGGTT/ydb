package com.lggtt.srb.core.controller.api;


import com.lggtt.srb.base.util.JwtUtils;
import com.lggtt.srb.core.pojo.entity.TransFlow;
import com.lggtt.srb.core.service.TransFlowService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 交易流水表 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "资金记录")
@CrossOrigin
@RestController
@RequestMapping("/api/core/transFlow")
public class TransFlowController {

    @Resource
    private TransFlowService transFlowService;

    @ApiOperation("获取列表")
    @GetMapping("/list")
    public R list(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<TransFlow> list = transFlowService.selectByUserId(userId);
        return R.ok().data("list", list);
    }
}

