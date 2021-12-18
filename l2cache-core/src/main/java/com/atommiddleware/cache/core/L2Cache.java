package com.atommiddleware.cache.core;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class L2Cache implements Cache {
    private String name;
    private Cache localCache;
    private Cache remoteCache;

    public L2Cache(String name, Cache localCache, Cache remoteCache) {
        this.name = name;
        this.localCache = localCache;
        this.remoteCache = remoteCache;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper =localCache.get(key);
        if (valueWrapper == null) {
            valueWrapper = remoteCache.get(key);
            if (valueWrapper != null) {
                localCache.put(key, valueWrapper.get());
            }
        }
        return valueWrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = localCache.get(key, type);
        if (value == null) {
            value = remoteCache.get(key, type);
            if (value != null) {
                localCache.put(key, value);
            }
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = localCache.get(key,valueLoader);
        if (value == null) {
            value = remoteCache.get(key, valueLoader);
            if (value != null) {
                localCache.put(key, value);
            }
        }
        return  value;
    }

    @Override
    public void put(Object key, Object value) {
        localCache.put(key, value);
        remoteCache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        localCache.putIfAbsent(key, value);
        return remoteCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        localCache.evict(key);
        remoteCache.evict(key);
    }

    @Override
    public void clear() {
        localCache.clear();
        remoteCache.clear();
    }
}
