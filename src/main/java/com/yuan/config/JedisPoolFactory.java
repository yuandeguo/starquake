package com.yuan.config;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/7 16:07
 * @Description 优化不用这个了
 */
import org.springframework.beans.factory.annotation.Value;


//@Configuration
public class JedisPoolFactory {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    /**
     * 初始化Redis连接池
     */
//    @Bean
//    public JedisPool generateJedisPoolFactory() {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(maxActive);
//        poolConfig.setMaxIdle(maxIdle);
//        poolConfig.setMinIdle(minIdle);
//        poolConfig.setMaxWait(Duration.ofMillis(maxWaitMillis));
//        // 连接耗尽时是否阻塞, false报异常,true阻塞直到超时, 默认true
//        poolConfig.setBlockWhenExhausted(Boolean.TRUE);
//        JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout,password);
//        return jedisPool;
//    }
}
