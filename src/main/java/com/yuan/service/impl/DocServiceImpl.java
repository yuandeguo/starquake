package com.yuan.service.impl;

import com.yuan.params.PictureHandleParam;
import com.yuan.service.DocService;
import com.yuan.thread.DocHandleThread;
import com.yuan.tool.PictureUtil;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.naming.directory.DirContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/15 22:38
 * @Description null
 */
@Service
public class DocServiceImpl implements DocService {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public String pictureHandle(PictureHandleParam param) throws IOException {
        MultipartFile file = param.getFile();
        InputStream inputStream = file.getInputStream();
        File dir = new File("D:\\personalProject\\blog\\rear\\starquake\\src\\inputPic\\");
        String suffix= param.getFormat();
        if(StringUtils.isBlank(suffix)){
            suffix = getSuffix(file.getOriginalFilename());
        }
        else {
            suffix="."+suffix;
        }
        String outputFileName;

        File output = File.createTempFile("pic", suffix, dir);
        outputFileName=output.getName();
        output.deleteOnExit();
        Thumbnails.Builder<? extends InputStream> handleFile = Thumbnails.of(inputStream);
        Integer pictureSizeType = param.getPictureSizeType();
        switch (pictureSizeType) {
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
        if (!StringUtils.isBlank(param.getFormat())) {
            handleFile = handleFile.outputFormat(param.getFormat());

        }
        if (param.getQuality() != null) {
            handleFile = handleFile.outputQuality(param.getQuality());

        }
        if (param.getAngel() != null) {
            handleFile.rotate(param.getAngel());
        }
        BufferedImage bufferedImage = handleFile
                .asBufferedImage();
        ImageIO.write(bufferedImage, suffix.substring(1), output);
        inputStream.close();
        return outputFileName;
    }

    @Override
    public void pictureHandleTemp(MultipartFile[] param) throws IOException {


    }
    private String getSuffix(String path)
    {   String suffix = path.substring(path.lastIndexOf("."));
        return suffix;
    }

}
