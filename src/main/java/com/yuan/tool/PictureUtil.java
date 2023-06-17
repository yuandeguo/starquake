package com.yuan.tool;

import java.io.File;
import java.io.IOException;

import com.yuan.params.PictureHandleParam;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/14 22:45
 * @Description null
 */
public class PictureUtil {
    public static void main(String[] args) throws IOException {
        //缩略图
        String source="D:\\myProgram\\myDemo\\MyDemo\\src\\main\\zinPicture\\1.JPG";
        String target="D:\\myProgram\\myDemo\\MyDemo\\src\\main\\zoutPicture\\1.jpg";
        setSize(source,target,2000,2000);
        target="D:\\myProgram\\myDemo\\MyDemo\\src\\main\\zoutPicture\\2.jpg";
        forceSetSize(source,target,200,200);
        target="D:\\myProgram\\myDemo\\MyDemo\\src\\main\\zoutPicture\\3.jpg";
        setRate(source,target,2);
        target="D:\\myProgram\\myDemo\\MyDemo\\src\\main\\zoutPicture\\4.jpg";
        zipQuality(source,target,0.1);
        target="D:\\myProgram\\myDemo\\MyDemo\\src\\main\\zoutPicture\\5";
        formatImage(source,target,"png");
        target="D:\\myProgram\\myDemo\\MyDemo\\src\\main\\zoutPicture\\6";
        rotate(source,target,90);
    }

    /**
     *  指定宽高压缩图片, 指定高度或宽度 ，哪个小那个生效，一样的话宽度生效。
     */
    public  static void setSize(String source,String target,int width,int height) throws IOException {

        Thumbnails.of(source)
                .size(width, height)
                .toFile(target);
    }

    /**
     * 强制  宽高
     * @param source
     * @param target
     * @param weight
     * @param height
     * @throws IOException
     */
    public static void forceSetSize(String source,String target,int weight,int height) throws IOException {

        Thumbnails.of(source)
                .forceSize(weight, height)
                .toFile(target);
    }

    /**
     * 按比例缩放
     * @param source
     * @param target
     * @param rate
     * @throws IOException
     */
    public static void setRate(String source,String target,double rate) throws IOException {
        Thumbnails.of(source)
                .scale(rate)
                .toFile(target);
    }

    /**
     * 0-1
     * 参数1为最高质量
     * @param source
     * @param target
     * @param quality
     * @throws IOException
     */
    public static void zipQuality(String source,String target,double quality) throws IOException {
        Thumbnails.of(source)
                .scale(1.0)
                .outputQuality(quality)
                .toFile(target);
    }

    /**
     * 文件格式的转化
     * JPG jpg bmp BMP gif GIF WBMP png PNG jpeg wbmp JPEG
     * @param source
     * @param target
     * @param format
     * @throws IOException
     */
    public static void formatImage(String source,String target,String format) throws IOException {

        Thumbnails.of(source)
                .scale(1.0)
                // 如果不设置默认跟原图片一致
                .outputFormat(format)
                // 设置质量
                .outputQuality(1F)
                .toFile(target);
    }

    /**
     * 图片旋转，angle 为旋转的角度
     * @param source
     * @param target
     * @param angle
     * @throws IOException
     */
    public static void rotate(String source,String target,double angle) throws IOException {
        Thumbnails.of(source)
                .scale(1.0)
                .rotate(angle)
                .toFile(target);
    }


    public static void pictureHandle(PictureHandleParam param,String source,String target) throws IOException {

        Thumbnails.Builder<File> handleFile = Thumbnails.of(source);
        Integer pictureSizeType= param.getPictureSizeType();
        switch (pictureSizeType)
        {
            case 1:
                handleFile = handleFile.size(param.getWidth(), param.getHeight());
                break;
            case 2:
                handleFile = handleFile.forceSize(param.getWidth(), param.getHeight());
                break;
            case 3:
                handleFile = handleFile.scale(param.getRate());
                break;
        }
        if(!StringUtils.isBlank(param.getFormat()))
        {
            handleFile = handleFile.outputFormat(param.getFormat());

        }
        if(param.getQuality()!=null)
        {
            handleFile = handleFile.outputQuality(param.getQuality());

        }
        if(param.getAngle()!=null)
        {
            handleFile.rotate(param.getAngle());
        }
        handleFile.toFile(target);

    }




}
