package com.yuan.service.impl;

import com.yuan.myEnum.CommonConst;
import com.yuan.params.PictureHandleParam;
import com.yuan.service.DocService;
import com.yuan.service.RedisService;
import com.yuan.task.DelayedTaskManager;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

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
    @Autowired
    RedisService redisService;
    @Autowired
    DelayedTaskManager delayedTaskManager;
    @Override
    public String pictureHandle(PictureHandleParam param) throws IOException {
        MultipartFile file = param.getFile();
        InputStream inputStream = file.getInputStream();
        File dir = new File(CommonConst.TEMP_DIR_PATh);
        String suffix= param.getFormat();
        if(StringUtils.isBlank(suffix)){
            suffix = getSuffix(Objects.requireNonNull(file.getOriginalFilename()));
        }
        else {
            suffix="."+suffix;
        }
        String outputFileName;

        File output = File.createTempFile(param.getToken(), suffix, dir);
        outputFileName=output.getName();
        output.deleteOnExit();
        delayedTaskManager.scheduleTask(param.getToken(),outputFileName,CommonConst.TOKEN_INTERVAL);
        Thumbnails.Builder<? extends InputStream> handleFile = Thumbnails.of(inputStream);
        Integer pictureSizeType = param.getPictureSizeType();
        switch (pictureSizeType) {
            case 0:
                handleFile = handleFile.scale(1.0);
                break;
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
        if (param.getQuality() != null&&param.getQuality()>0&&param.getQuality()<=1.0) {
            handleFile = handleFile.outputQuality(param.getQuality());
        }
        if (param.getAngle() != null&&param.getAngle()>0) {
            handleFile.rotate(param.getAngle());
        }
        handleFile.toFile(output);
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
