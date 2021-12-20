package com.atommiddleware.autoconfigure.cache;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.atommiddleware.cache.core.L2CacheConfig;
/**
 * Configure the CacheManager of the L2 cache
 * @author ruoshui
 *
 */
@Configuration
@ConditionalOnBean(name = L2CacheConfig.L2CACHE_CACHE_MANAGER)
public class EnableCachingImportConfiguration {

	@Primary
	@Bean
	public CachingConfigurer cachingConfigurer(ObjectProvider<KeyGenerator> keyGenerator,
			ObjectProvider<CacheErrorHandler> cacheErrorHandler,
			@Qualifier(L2CacheConfig.L2CACHE_CACHE_MANAGER) CacheManager cacheManager,
			ObjectProvider<CacheResolver> cacheResolver) {
		return new CachingConfigurer() {
			@Override
			public KeyGenerator keyGenerator() {
				return keyGenerator.getIfAvailable();
			}

			@Override
			public CacheErrorHandler errorHandler() {
				return cacheErrorHandler.getIfAvailable();
			}

			@Override
			public CacheResolver cacheResolver() {
				if (null != cacheResolver.getIfAvailable()) {
					return cacheResolver.getIfAvailable();
				} else {
					return new SimpleCacheResolver(cacheManager);
				}
			}

			@Override
			public CacheManager cacheManager() {
				return cacheManager;
			}
		};
	}
	
	@Configuration
	@EnableCaching
	public class EnableCachingImportApplyConfiguration{
		
	}
}
