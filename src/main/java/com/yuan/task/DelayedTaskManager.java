package com.yuan.task;

import com.yuan.myEnum.CommonConst;
import lombok.Data;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.lang.module.Configuration;
import java.util.Set;

/**
 * @author yuanguozhi
 * @version V1.0
 * @date 2023/6/17 16:01
 * @Description null
 */
@Data
public class DelayedTaskManager {

    private StringRedisTemplate stringRedisTemplate;
    private DocDeleteProcessor docDeleteProcessor;

    public DelayedTaskManager(StringRedisTemplate stringRedisTemplate, DocDeleteProcessor docDeleteProcessor) {
        this.stringRedisTemplate =stringRedisTemplate;
        this.docDeleteProcessor = docDeleteProcessor;
    }
    // 延迟任务管理器
    public void scheduleTask(String docToken,String filePath, long delayInSeconds) {
        //当前时间
        long currentTime = System.currentTimeMillis();
        long delayInMillis = delayInSeconds * 1000;
        //过期时间
        long scheduleTime = currentTime + delayInMillis;
        stringRedisTemplate.opsForList().leftPush(CommonConst.TEMP_FILE_PREFIX+docToken, filePath);
        stringRedisTemplate.opsForZSet().add(CommonConst.DELAYED_DOC_TASKS, docToken, scheduleTime);
    }

    public void checkAndProcessDelayedTasks() {
        long currentTime = System.currentTimeMillis();
        Set<String> docTokens = stringRedisTemplate.opsForZSet().rangeByScore(CommonConst.DELAYED_DOC_TASKS, 0, currentTime);
        if (docTokens != null) {
            for (String docToken : docTokens) {
                docDeleteProcessor.deleteDoc(CommonConst.TEMP_FILE_PREFIX+docToken);
                stringRedisTemplate.delete(CommonConst.TEMP_FILE_PREFIX+docToken);
                stringRedisTemplate.opsForZSet().remove(CommonConst.DELAYED_DOC_TASKS, docToken);
            }
        }
    }


}
