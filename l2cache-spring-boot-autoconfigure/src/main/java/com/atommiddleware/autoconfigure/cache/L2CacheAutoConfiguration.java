package com.atommiddleware.autoconfigure.cache;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.atommiddleware.cache.core.L2CacheConfig;

@Configuration
@ConditionalOnProperty(prefix = "com.atommiddleware.l2cache", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(L2CacheConfig.class)
@Import({ L2CacheLettuceConnectionConfiguration.class, L2CacheJedisConnectionConfiguration.class,
		L2CacheEhcacheRedisConfiguration.class, EnableCachingImportConfiguration.class,
		L2CachingDataInterceptorConfiguration.class })
@AutoConfigureAfter(RedisAutoConfiguration.class)
@AutoConfigureBefore(RedisReactiveAutoConfiguration.class)
public class L2CacheAutoConfiguration {

}
