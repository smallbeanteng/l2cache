package com.atommiddleware.cache.core;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class L2CacheManager extends AbstractCacheManager {

    private final CacheManager localCacheManger;
    private final CacheManager remoteCacheManager;

    public L2CacheManager(CacheManager localCacheManager, CacheManager remoteCacheManager) {
        this.localCacheManger = localCacheManager;
        this.remoteCacheManager = remoteCacheManager;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Set<String> localCacheNames = new HashSet<>(localCacheManger.getCacheNames());
        Set<String> remoteCacheNames = new HashSet<>(remoteCacheManager.getCacheNames());
        Collection<Cache> caches = new ArrayList<>();
        localCacheNames.forEach(name -> {
            if (remoteCacheNames.contains(name)) {
                caches.add(new L2Cache(name, localCacheManger.getCache(name), remoteCacheManager.getCache(name)));
            }
        });
        return caches;
    }

    @Override
    protected Cache getMissingCache(String name) {
        return new L2Cache(name,localCacheManger.getCache(name),remoteCacheManager.getCache(name));
    }

}
