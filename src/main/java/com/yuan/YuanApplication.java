package com.yuan;

import com.alibaba.druid.filter.logging.LogFilter;
import com.yuan.filter.UrlFilter;
import com.yuan.listener.FirstListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/26 23:52
 * @Description
 *
 * EnableScheduling可以开启定时任务,一段时间运行一次某个函数
 * EnableAsync可以被用于需要执行一些比较耗时的操作，但又不希望阻塞主线程的情况
 */

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class YuanApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuanApplication.class, args);
    }


}
