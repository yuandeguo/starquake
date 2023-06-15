package com.yuan.service.impl;

import com.yuan.params.PictureHandleParam;
import com.yuan.service.DocService;
import com.yuan.tool.PictureUtil;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.directory.DirContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/15 22:38
 * @Description null
 */
@Service
public class DocServiceImpl implements DocService {


    @Override
    public void pictureHandle(PictureHandleParam param) throws IOException {
       InputStream[] list=new InputStream[ param.getFile().length];
        MultipartFile[] file = param.getFile();
        for(int i=0;i<file.length;i++){
        list[i]=file[i].getInputStream();
        }
        Thumbnails.Builder<? extends InputStream> handleFile = Thumbnails.of(list);
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
        if(param.getAngel()!=null)
        {
            handleFile.rotate(param.getAngel());
        }
        File outputDir = new File("D:\\my_java_project\\yuan_blog\\yuan_blog\\src\\inputPic");  // 输出文件夹

        handleFile.toFile(outputDir);
        for(int i=0;i<list.length;i++){
            list[i].close();
        }

    }
}
