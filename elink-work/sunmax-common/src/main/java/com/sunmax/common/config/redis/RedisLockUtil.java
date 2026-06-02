package com.sunmax.common.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RedisLockUtil {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;

//    @Value("${spring.redis.lettuce.pool.max-active}")
//    private int maxActive;
//
//    @Value("${spring.redis.lettuce.pool.max-wait}")
//    private int maxWait;


    private static RedissonClient redissonClient;

    @Bean
    public void init() {
        Config config = new Config();
        //单机模式
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://" + host + ":" + port);
        singleServerConfig.setDatabase(database);
        singleServerConfig.setPassword(password);
        singleServerConfig.setConnectionMinimumIdleSize(minIdle);
        singleServerConfig.setConnectionPoolSize(maxIdle);
        singleServerConfig.setRetryAttempts(3);
        singleServerConfig.setRetryInterval(1500);
        singleServerConfig.setTimeout(60000);
        singleServerConfig.setConnectTimeout(30000);
        singleServerConfig.setIdleConnectionTimeout(60000);
        RedisLockUtil.redissonClient = Redisson.create(config);
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey     锁的键
     * @param waitTimeout 等待获取锁的超时时间
     * @return 是否成功获取锁
     */
    public static boolean lock(String lockKey, long waitTimeout) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTimeout, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey     锁的键
     * @param waitTimeout 等待获取锁的超时时间
     * @param expireTime  锁的过期时间
     * @return 是否成功获取锁
     */
    public static boolean lock(String lockKey, long waitTimeout, long expireTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTimeout, expireTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey 锁的键
     */
    public static void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

}

