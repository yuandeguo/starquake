package com.yuan.service;

import com.yuan.params.PictureHandleParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/15 22:38
 * @Description null
 */
public interface DocService {
    String pictureHandle(PictureHandleParam pictureHandleParam) throws IOException;
    void pictureHandleTemp(MultipartFile[] param) throws IOException;
}
