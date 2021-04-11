package com.lggtt.srb.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.lggtt.srb.core.pojo.dto.ExcelDictDto;
import com.lggtt.srb.core.pojo.entity.Dict;
import com.lggtt.srb.core.service.DictService;
import com.lggtt.srb.exception.BusinessException;
import com.lggtt.srb.result.R;
import com.lggtt.srb.result.ResponseEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author lggtt
 * @since 2021-04-03
 */
@Api(tags = "数据字典管理")
@RestController
@RequestMapping("/admin/core/dict")
@CrossOrigin
public class AdminDictController {

    @Resource
    DictService dictService;

    @ApiOperation("excel 数据导入")
    @PostMapping("/import")
    public R batchImport(
            @ApiParam("excel 数据字典文件")
            @RequestParam("file") MultipartFile file
    ) {
        try {
            InputStream inputStream = file.getInputStream();
            dictService.importData(inputStream);
            return R.ok().message("数据导入成功");
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }


    @ApiOperation("Excel数据的导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        try {
            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDto.class).sheet("数据字典").doWrite(dictService.listDictData());
        } catch (IOException e) {
            //EXPORT_DATA_ERROR(104, "数据导出失败"),
            throw new BusinessException(ResponseEnum.EXPORT_DATA_ERROR, e);
        }
    }

    @ApiOperation("获取指定id的字典数据")
    @GetMapping("/get/{parent_id}")
    public R listByParentId(@ApiParam("字典父id")
                            @PathVariable("parent_id")Long parentId) {
        List<Dict> dict = dictService.listByParentId(parentId);
        return R.ok().data("list",dict);
    }


}

