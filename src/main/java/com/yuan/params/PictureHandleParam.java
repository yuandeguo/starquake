package com.yuan.params;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/14 23:11
 * @Description null
 */
@Data
public class PictureHandleParam implements Serializable {
     public static final Long serialVersionUID = 1L;
    /**
     * 1 - 指定宽高压缩图片, 指定高度或宽度 ，哪个小那个生效，一样的话宽度生效。等比例
     * 2 - 强制  宽高  压缩图片
     * 3 - 按比例缩放
     */
    Integer pictureSizeType;
    //1 - 指定宽高压缩图片, 指定高度或宽度 ，哪个小那个生效，一样的话宽度生效。等比例   宽度 高度  ，2 强制  宽高  压缩图片
    Integer width;
    Integer height;
    // 3 - 按比例缩放
    Double rate;

    /**
     * 文件格式的转化JPG jpg bmp BMP gif GIF WBMP png PNG jpeg wbmp JPEG
     */
    String  format;

    /**
     * 图片质量压缩
     *  参数1为最高质量
     */
    Double quality;

    /**
     *  图片旋转，angle 为旋转的角度
     */
    Double angel;


    MultipartFile file;



}
