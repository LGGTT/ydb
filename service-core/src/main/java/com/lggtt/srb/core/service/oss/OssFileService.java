package com.lggtt.srb.core.service.oss;

import java.io.InputStream;

public interface OssFileService {

    /**
     *  文件上传到 aliyun-oss
     *
     */
    String upload(InputStream inputStream,String module,String fileName);

    void removeOss(String url);
}
