package com.yuan.listener;

import com.yuan.myEnum.CommonConst;
import com.yuan.service.RedisService;
import com.yuan.service.WebInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/8 16:18
 * @Description null
 */
@Service
@Slf4j
public class FirstListener implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private WebInfoService webInfoService;
    @Resource
    private RedisService redisService;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        webInfoService.getWebInfo();
        redisService.getVisitUrl();

        String folderPath = CommonConst.TEMP_PIC_DIR_PATh; // 设置文件夹路径
        Path folder = Paths.get(folderPath);

        try {
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
                System.out.println("Folder created successfully.");
            } else {
                System.out.println("Folder already exists.");
            }
        } catch (Exception e) {
            System.out.println("Failed to create folder: " + e.getMessage());
        }

    }



}
