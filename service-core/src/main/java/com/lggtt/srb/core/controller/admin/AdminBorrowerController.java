package com.lggtt.srb.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lggtt.srb.core.pojo.entity.Borrower;
import com.lggtt.srb.core.pojo.vo.BorrowerApprovalVO;
import com.lggtt.srb.core.pojo.vo.BorrowerDetailVO;
import com.lggtt.srb.core.service.BorrowerService;
import com.lggtt.srb.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "借款人后台管理")
@CrossOrigin
@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {

    @Resource
    BorrowerService borrowerService;

    @ApiOperation("获取借款人列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页数",required = true)
                      @PathVariable("page")Long page,

                      @ApiParam(value = "每页记录数" ,required = true)
                      @PathVariable("limit")Long limit,

                      @ApiParam("搜索关键词")
                      @RequestParam String keyword){

        Page<Borrower> pageParam = new Page<>(page, limit);
        IPage<Borrower> pageModel = borrowerService.listPage(pageParam,keyword);
        return R.ok().data("pageModel",pageModel);
    }

    @ApiOperation("获取借款人详情")
    @GetMapping("/show/{id}")
    public R showDetail(@ApiParam(value = "借款人 id",required = true)
                  @PathVariable("id")Long id){

        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);
        return R.ok().data("borrowerDetailVO",borrowerDetailVO);

    }

    @ApiOperation("借款人信息审批")
    @PostMapping("/approval")
    public R approval(@ApiParam(value = "借款人审批信息",required = true)
                      @RequestBody BorrowerApprovalVO borrowerApprovalVO){

        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("审批完成");
    }

}
