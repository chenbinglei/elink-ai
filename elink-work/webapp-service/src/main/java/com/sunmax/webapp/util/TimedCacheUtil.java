package com.sunmax.webapp.util;

import java.util.HashMap;
import java.util.Map;

public class TimedCacheUtil<K, V> {

    private final Map<K, V> cache = new HashMap<>();
    private final Map<K, Long> expireTimes = new HashMap<>();

    public V get(K key) {
        Long expireTime = expireTimes.get(key);
        if (expireTime != null && System.currentTimeMillis() > expireTime) {
            cache.remove(key);
            expireTimes.remove(key);
            return null;
        }
        return cache.get(key);
    }

    public void put(K key, V value, Integer seconds) {
        cache.put(key, value);
        long expireTime = System.currentTimeMillis() + seconds * 1000L;
        expireTimes.put(key, expireTime);
    }

    public Boolean containsKey(K key) {
        if (!expireTimes.containsKey(key)) {
            cache.remove(key);
            return false;
        }
        if (!cache.containsKey(key)) {
            expireTimes.remove(key);
            return false;
        }
        Long expireTime = expireTimes.get(key);
        if (expireTime != null && System.currentTimeMillis() > expireTime) {
            cache.remove(key);
            expireTimes.remove(key);
            return false;
        }
        return true;
    }

    public void remove(K key) {
        cache.remove(key);
        expireTimes.remove(key);
    }

}
