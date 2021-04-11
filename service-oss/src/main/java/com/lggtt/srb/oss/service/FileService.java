package com.lggtt.srb.oss.service;

import java.io.InputStream;

public interface FileService {

    /**
     *  文件上传到 aliyun-oss
     *
     */
    String upload(InputStream inputStream,String module,String fileName);

    void removeOss(String url);
}
