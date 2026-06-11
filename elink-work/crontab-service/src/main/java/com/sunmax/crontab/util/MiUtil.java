package com.sunmax.crontab.util;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class MiUtil {

    public static final Lock lock = new ReentrantLock();

    public static long lastTimestamp = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS")) + "001"); // 初始时间戳，单位：秒

    public static long getUniqueTimestamp() {
        lock.lock();
        try {
            long currentTimestamp = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS")) + "001");
            if (currentTimestamp <= lastTimestamp) {
                // 如果当前时间戳小于等于上一个时间戳，则将上一个时间戳加1，保证唯一性
                lastTimestamp++;
            } else {
                lastTimestamp = currentTimestamp;
            }
            return lastTimestamp;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        log.info("{}", getUniqueTimestamp());
    }

}
