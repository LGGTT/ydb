package com.lggtt.srb.core.controller.api;


import com.lggtt.srb.base.util.JwtUtils;
import com.lggtt.srb.core.pojo.vo.BorrowerVO;
import com.lggtt.srb.core.service.BorrowerService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "借款人信息")
@CrossOrigin
@RestController
@RequestMapping("/api/core/borrower")
public class BorrowerController {

    @Resource
    BorrowerService borrowerService;

    @ApiOperation("借款人信息提交")
    @PostMapping("/auth/save")
    public R save(@ApiParam(value = "信息提交",required = true)
                  @RequestBody BorrowerVO borrowerVO,
                  HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowerService.saveBorrowByUserId(userId,borrowerVO);

        return R.ok().message("提交成功");
    }

    @ApiOperation("获取借款人状态")
    @GetMapping("/auth/getBorrowerStatus")
    public R getStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        Integer status = borrowerService.getStatusByUserId(userId);
        return R.ok().data("status",status);
    }
}

