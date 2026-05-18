package com.neasaa.base.app.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache {
    private static volatile SimpleCache instance;

    private static final long DEFAULT_TTL_MILLIS = 10 * 60 * 1000; // 10 minutes

    private static class CacheEntry {
        Object value;
        long expiryTime; // -1 means never expire

        CacheEntry(Object value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long defaultTtlMillis;

    private SimpleCache(long defaultTtlMillis) {
        this.defaultTtlMillis = defaultTtlMillis;
    }

    public static SimpleCache getInstance() {
        if (instance == null) {
            synchronized (SimpleCache.class) {
                if (instance == null) {
                    instance = new SimpleCache(DEFAULT_TTL_MILLIS);
                }
            }
        }
        return instance;
    }

    // Static put with default TTL
    public static void put(String key, Object value) {
        getInstance().putLocal(key, value);
    }

    // Static put with custom TTL
    public static void put(String key, Object value, long ttlMillis) {
        getInstance().putLocal(key, value, ttlMillis);
    }

    // Static get with type
    public static <T> T get(String key, Class<T> type) {
        return getInstance().getLocal(key, type);
    }

    // Instance put with default TTL
    private void putLocal(String key, Object value) {
        putLocal(key, value, defaultTtlMillis);
    }

    // Instance put with custom TTL, -1 for never expire
    private void putLocal(String key, Object value, long ttlMillis) {
        long expiryTime = (ttlMillis == -1) ? -1 : System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry(value, expiryTime));
    }

    private <T> T getLocal(String key, Class<T> type) {
        CacheEntry entry = cache.get(key);
        if (entry == null) return null;
        if (entry.expiryTime != -1 && System.currentTimeMillis() > entry.expiryTime) {
            cache.remove(key);
            return null;
        }
        if (type.isInstance(entry.value)) {
            return type.cast(entry.value);
        }
        return null;
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}