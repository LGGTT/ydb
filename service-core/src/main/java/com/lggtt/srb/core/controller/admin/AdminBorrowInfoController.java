package com.lggtt.srb.core.controller.admin;


import com.lggtt.srb.base.util.JwtUtils;
import com.lggtt.srb.core.pojo.entity.BorrowInfo;
import com.lggtt.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.lggtt.srb.core.service.BorrowInfoService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "借款管理")
@CrossOrigin
@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController {

    @Resource
    BorrowInfoService borrowInfoService;

    @ApiOperation("借款信息列表")
    @GetMapping("/list")
    public R list(){
        List<BorrowInfo> list = borrowInfoService.selectList();
        return R.ok().data("list",list);
    }

    @ApiOperation("借款信息详情")
    @GetMapping("/show/{id}")
    public R show(@ApiParam(value = "借款信息id",required = true)
                  @PathVariable("id") Long id){

        Map<String,Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return R.ok().data("borrowInfoDetail",borrowInfoDetail);
    }

    @ApiOperation("借款信息审批")
    @PostMapping("/approval")
    public R approval(@ApiParam(value = "借款审批信息",required = true)
                  @RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO){

        borrowInfoService.approval(borrowInfoApprovalVO);
        return R.ok().message("审批完成");
    }

}

