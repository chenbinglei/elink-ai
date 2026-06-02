package com.sunmax.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimestampUtil {

    public static final Lock lock = new ReentrantLock();

    public static Integer lastTimestamp = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("ssSSS")) + "001"); // 初始时间戳，单位：秒

    public static Integer getUniqueTimestamp() {
        lock.lock();
        try {
            int currentTimestamp = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("ssSSS")) + "001");
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
        System.out.println(getUniqueTimestamp());
    }

}
