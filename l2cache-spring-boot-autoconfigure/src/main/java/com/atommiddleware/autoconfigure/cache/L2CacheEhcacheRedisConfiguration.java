package com.atommiddleware.autoconfigure.cache;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.jcache.JCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import com.atommiddleware.cache.core.L2CacheConfig;
import com.atommiddleware.cache.core.L2CacheManager;

@Configuration
@ConditionalOnProperty(prefix = "com.atommiddleware.l2cache.cacheConfig", name = "enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(name = L2CacheConfig.L2CACHE_REMOTE_REDIS_CONNECTION_FACTORY)
@ConditionalOnClass(name = { "org.springframework.data.redis.connection.RedisConnectionFactory",
		"org.ehcache.config.builders.CacheConfigurationBuilder" })
public class L2CacheEhcacheRedisConfiguration {

	@Autowired
	private L2CacheConfig l2CacheConfig;
	
	@Qualifier(L2CacheConfig.L2CACHE_REMOTE_REDIS_CONFIGURATION)
	@Autowired(required = false)
	private RedisCacheConfiguration l2CacheRemoteRedisConfiguration;

	@Bean(name = L2CacheConfig.L2CACHE_REMOTE_CACHE_MANAGER)
	@ConditionalOnMissingBean(name = L2CacheConfig.L2CACHE_REMOTE_CACHE_MANAGER)
	public RedisCacheManager remotecacheManager(
			@Qualifier(L2CacheConfig.L2CACHE_REMOTE_REDIS_CONNECTION_FACTORY) RedisConnectionFactory redisConnectionFactory,
			ResourceLoader resourceLoader) {
		Duration ttl = Duration.ofSeconds(L2CacheConfig.DEFAULT_REMOTE_TIMEOUT);
		if (l2CacheConfig.getCacheConfig().getDefaultTimeout() > 0) {
			ttl = Duration.ofSeconds(l2CacheConfig.getCacheConfig().getDefaultTimeout());
		}
		RedisCacheConfiguration cacheConfiguration = null;
		if (null != l2CacheRemoteRedisConfiguration) {
			cacheConfiguration = l2CacheRemoteRedisConfiguration;
		}
		if (null == cacheConfiguration) {
			cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
					.serializeValuesWith(RedisSerializationContext.SerializationPair
							.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		}
		cacheConfiguration.entryTtl(ttl);
		RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
				.cacheDefaults(cacheConfiguration).build();
		return redisCacheManager;
	}

	@Bean
	public JCacheManagerFactoryBean cacheManagerFactoryBean() throws IOException {
		JCacheManagerFactoryBean factoryBean = new JCacheManagerFactoryBean();
		factoryBean.setCacheManagerUri(new ClassPathResource("ehcache.xml").getURI());
		return factoryBean;
	}

	@Bean(name = L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER)
	@ConditionalOnMissingBean(name = L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER)
	public CacheManager localCacheManager(javax.cache.CacheManager cacheManager) {
		JCacheCacheManager cacheCacheManager = new JCacheCacheManager();
		cacheCacheManager.setCacheManager(cacheManager);
		return cacheCacheManager;
	}

	@Bean(name = L2CacheConfig.L2CACHE_CACHE_MANAGER)
	@ConditionalOnMissingBean(name = L2CacheConfig.L2CACHE_CACHE_MANAGER)
	public CacheManager cacheManager(
			@Qualifier(L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER) CacheManager localCacheManager,
			@Qualifier(L2CacheConfig.L2CACHE_REMOTE_CACHE_MANAGER) CacheManager remoteCacheManager) {
		return new L2CacheManager(localCacheManager, remoteCacheManager);
	}

	@Bean
	@ConditionalOnMissingBean
	public ScheduledExecutorService scheduledHandleExpire() {
		return Executors.newSingleThreadScheduledExecutor();
	}

	@ConditionalOnProperty(prefix = "com.atommiddleware.l2cache.cacheConfig", name = "enableRedisEvent", havingValue = "true")
	@ConditionalOnMissingBean(name = L2CacheConfig.L2CACHE_HANDLE_EXPIRE_KEY)
	@Bean(name = L2CacheConfig.L2CACHE_HANDLE_EXPIRE_KEY)
	public MessageListener messageListener(
			@Qualifier(L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER) CacheManager localCacheManager,
			ScheduledExecutorService scheduledHandleExpire) {
		return (Message message, @Nullable byte[] pattern) -> {
			if (null != message) {
				String expireMessage = message.toString();
				if (StringUtils.hasText(expireMessage)) {
					String[] prefixKey = expireMessage.split(L2CacheConfig.KEY_PREFIX);
					if (null != prefixKey && prefixKey.length == 2) {
						Cache cache = localCacheManager.getCache(prefixKey[0]);
						if (null != cache) {
							// Avoid concurrent deletion of local cache and impact on remote cache
							scheduledHandleExpire.schedule(() -> cache.evict(prefixKey[1]),
									ThreadLocalRandom.current().nextInt(1000), TimeUnit.MILLISECONDS);
						}
					}
				}
			}
		};
	}

	@Bean
	@ConditionalOnProperty(prefix = "com.atommiddleware.l2cache.cacheConfig", name = "enableRedisEvent", havingValue = "true")
	RedisMessageListenerContainer redisMessageListenerContainer(
			@Qualifier(L2CacheConfig.L2CACHE_REMOTE_REDIS_CONNECTION_FACTORY) RedisConnectionFactory connectionFactory,
			MessageListener messageListener) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(messageListener, new PatternTopic("__keyevent@0__:expired"));
		return container;
	}

	@Bean
	@ConditionalOnMissingBean(name = "reactiveRedisTemplate")
	@ConditionalOnBean(ReactiveRedisConnectionFactory.class)
	public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(
			ReactiveRedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
		JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(
				resourceLoader.getClassLoader());
		RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
				.newSerializationContext().key(jdkSerializer).value(jdkSerializer).hashKey(jdkSerializer)
				.hashValue(jdkSerializer).build();
		return new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
	}
}
