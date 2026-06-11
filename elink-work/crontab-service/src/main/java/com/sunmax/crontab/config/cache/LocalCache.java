package com.sunmax.crontab.config.cache;

import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
* 本地缓存的实现类
*/
@Component
@Slf4j
public class LocalCache {

    //默认的缓存容量
    private static int DEFAULT_CAPACITY = 512;
    //最大容量
    private static int MAX_CAPACITY = 100000;
    //刷新缓存的频率
    private static int MONITOR_DURATION = 2;
    // 启动监控线程
//    static {
//        new Thread(new TimeoutTimerThread()).start();
//    }
    //使用默认容量创建一个Map
    private static final ConcurrentHashMap<String, CacheEntity> cache = new ConcurrentHashMap<>(DEFAULT_CAPACITY);

    /**
     * 将key-value 保存到本地缓存并制定该缓存的过期时间
     *
     * @param key
     * @param value
     * @param expireTime 过期时间，如果是-1 则表示永不过期
     * @return
     */
    public boolean putValue(String key, LocalCacheDto value, int expireTime) {
        return putCloneValue(key, value, expireTime);
    }

    /**
     * 将值通过序列化clone 处理后保存到缓存中，可以解决值引用的问题
     *
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    private boolean putCloneValue(String key, LocalCacheDto value, int expireTime) {
        try {
            if (cache.size() >= MAX_CAPACITY) {
                return false;
            }
            // 序列化赋值
            CacheEntity entityClone = clone(new CacheEntity(value, System.nanoTime(), expireTime));
            cache.put(key, entityClone);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     *
     * 序列化 克隆处理
     * @param object
     * @return
     */
    private <T extends Serializable> T clone(T object) {
        T cloneObject = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            cloneObject = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return cloneObject;
    }

    /**
     *从本地缓存中获取key对应的值，如果该值不存则则返回null
     *
     * @param key
     * @return
     */
    public LocalCacheDto getValue(String key) {
        CacheEntity cacheEntity = cache.get(key);
        if (StringUtil.isNotEmpty(cacheEntity)) {
            return cacheEntity.getValue();
        }
        return null;

    }

    /**
     * 清空所有
     */
    public void clear() {
        cache.clear();
    }

    /**
     * 根据key删除缓存
     */
    public boolean removeCache(String key) {
        cache.remove(key);
        return true;
    }

    /**
     * 过期处理线程
     */
    static class TimeoutTimerThread implements Runnable {
        public void run() {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(MONITOR_DURATION);
                    checkTime();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error(e.getMessage(), e);
                } catch (RuntimeException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        /**
         * 过期缓存的具体处理方法
         */
        private void checkTime() {
            //"开始处理过期 "
            for (String key : cache.keySet()) {
                CacheEntity tce = cache.get(key);
                long timoutTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()
                        - tce.getGmtModify());
                //" 过期时间 : "+timoutTime);
                if (tce.getExpire() > timoutTime) {
                    continue;
                }
                log.info(" 清除过期缓存 ： " + key);
                //清除过期缓存和删除对应的缓存队列
                cache.remove(key);
            }
        }
    }
}