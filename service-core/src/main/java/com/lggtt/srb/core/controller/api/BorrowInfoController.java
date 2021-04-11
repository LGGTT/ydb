package com.lggtt.srb.core.controller.api;


import com.baomidou.mybatisplus.annotation.TableField;
import com.lggtt.srb.base.util.JwtUtils;
import com.lggtt.srb.core.pojo.entity.BorrowInfo;
import com.lggtt.srb.core.service.BorrowInfoService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "借款信息")
@CrossOrigin
@RestController
@RequestMapping("/api/core/borrowInfo")
public class BorrowInfoController {

    @Resource
    BorrowInfoService borrowInfoService;

    @ApiOperation("获取借款人额度")
    @GetMapping("/auth/getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest request){

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return R.ok().data("borrowAmount",borrowAmount);
    }

    @ApiOperation("借款申请提交")
    @PostMapping("/auth/save")
    public R save(
            @ApiParam("借款申请信息")
            @RequestBody BorrowInfo borrowInfo,HttpServletRequest request){

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowInfoService.saveBorrowInfo(borrowInfo,userId);

        return R.ok().message("提交成功");

    }

    @ApiOperation("获取借款申请审批状态")
    @GetMapping("/auth/getBorrowInfoStatus")
    public R getBorrowerStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowInfoService.getStatusByUserId(userId);
        return R.ok().data("borrowInfoStatus", status);
    }
}

