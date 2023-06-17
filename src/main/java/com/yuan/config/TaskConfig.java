package com.yuan.config;

import com.yuan.task.DelayedTaskManager;
import com.yuan.task.DocDeleteProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author yuanguozhi
 * @version V1.0
 * @date 2023/6/17 16:20
 * @Description null
 */
@Configuration
public class TaskConfig {
    @Bean
    public DocDeleteProcessor docDeleteProcessor(StringRedisTemplate stringRedisTemplate){
        return new DocDeleteProcessor(stringRedisTemplate);
    }
    @Bean
    public DelayedTaskManager delayedTaskManager(StringRedisTemplate stringRedisTemplate, DocDeleteProcessor docDeleteProcessor){
        return new DelayedTaskManager(stringRedisTemplate,docDeleteProcessor);
    }



}
