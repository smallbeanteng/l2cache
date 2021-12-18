package com.atommiddleware.autoconfigure.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.atommiddleware.cache.core.L2CacheConfig;
import com.atommiddleware.cache.intercept.BloomFilterDataInterceptor;
import com.atommiddleware.cache.intercept.DataInterceptor;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

@Configuration
@ConditionalOnClass(name = "com.google.common.hash.BloomFilter")
@ConditionalOnProperty(prefix = "com.atommiddleware.l2cache.dataInterceptor",name = "enable", havingValue = "true", matchIfMissing = true)
@Import(L2CacheDataInterceptorImportBeanDefinitionRegistrar.class)
public class L2CachingDataInterceptorConfiguration {

    @Autowired
    private L2CacheConfig l2CacheConfig;

    @Bean
    @ConditionalOnMissingBean
    public DataInterceptor dataInterceptor() {
        int maxBalckList=L2CacheConfig.DEFAULT_MAX_BALCK_LIST;
        if (null!=l2CacheConfig.getDataInterceptor()&&l2CacheConfig.getDataInterceptor().getMaxBlackList()>0){
            maxBalckList=l2CacheConfig.getDataInterceptor().getMaxBlackList();
        }
        return new BloomFilterDataInterceptor(bloomFilter(), new ConcurrentHashMap<String, String>(),maxBalckList);
    }

    @Bean
    @ConditionalOnMissingBean
    public BloomFilter<String> bloomFilter() {
        L2CacheConfig.DataInterceptConfig dataInterceptConfig = l2CacheConfig.getDataInterceptor();
        int expectedInsertions = L2CacheConfig.DEFAULT_EXPECTEDINSERTIONS;
        double fpp = L2CacheConfig.DEFAULT_FPP;
        if (null != dataInterceptConfig) {
            if (dataInterceptConfig.getExpectedInsertions() > 0) {
                expectedInsertions = dataInterceptConfig.getExpectedInsertions();
            }
            if (dataInterceptConfig.getFpp() > 0) {
                fpp = dataInterceptConfig.getFpp();
            }
        }
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedInsertions, fpp);
        return bloomFilter;
    }
}
