package com.yuan.service;

import com.yuan.params.PictureHandleParam;

import java.io.IOException;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/15 22:38
 * @Description null
 */
public interface DocService {
    void pictureHandle(PictureHandleParam pictureHandleParam) throws IOException;
}
