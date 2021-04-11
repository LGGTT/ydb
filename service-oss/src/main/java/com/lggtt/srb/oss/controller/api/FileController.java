package com.lggtt.srb.oss.controller.api;


import com.lggtt.srb.exception.BusinessException;
import com.lggtt.srb.oss.service.FileService;
import com.lggtt.srb.result.R;
import com.lggtt.srb.result.ResponseEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@Api(tags = "oss文件上传接口")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/oss/file")
public class FileController {
    @Resource
    FileService fileService;

    @ApiOperation("oss文件上传")
    @PostMapping("/upload")
    public R upload(
            @ApiParam(value = "文件",required = true)
            @RequestParam("file")MultipartFile file,

            @ApiParam(value = "模块",required = true)
            @RequestParam("module")String module){
        try {
            InputStream inputStream = file.getInputStream();
            String url = fileService.upload(inputStream, module, file.getOriginalFilename());
            return R.ok().message("文件上传成功").data("url",url);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }

    @ApiOperation("删除oss文件")
    @DeleteMapping("/remove")
    public R remove(@ApiParam(value = "文件 oss 地址",required = true)
                    @RequestParam("url")String url){
        fileService.removeOss(url);
        return R.ok().message("删除成功");
    }

}
