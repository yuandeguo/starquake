package com.yuan.listener;

import com.yuan.service.RedisService;
import com.yuan.service.WebInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


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
    }
}
