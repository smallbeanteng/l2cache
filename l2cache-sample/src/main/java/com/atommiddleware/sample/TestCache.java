package com.atommiddleware.sample;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.atommiddleware.cache.core.L2CacheConfig;

/**
 * Test L2 cache
 * @author ruoshui
 *
 */
@Component
public class TestCache {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * The cache cacheNames configured with l2cache are in ehCache Configuration in XML
	 * 
	 * @param id
	 * @return
	 */
	@Cacheable(cacheNames = "t123", key = "#id")
	public String getSample(String id) {
		System.out.println("id The obtained results will be cached in L2:" + id);
		return String.valueOf(ThreadLocalRandom.current().nextInt(1000));
	}
	
	/**
	 * Using spring Redis configured cache
	 * @param id
	 * @return
	 */
	public String getSampleDefaultRedisConfiguration(String id) {
		System.out.println("Using spring Redis default configuration cache");
		stringRedisTemplate.opsForValue().set(id, id);
		return stringRedisTemplate.opsForValue().get(id);
	}
	/**
	 * ehcache3 is used alone
	 * @return
	 */
	@Cacheable(cacheManager =L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER,cacheNames = "t123", key = "#id")
	public String getEhcache3Cache() {
		return "result";
	}
	/**
	 * redis is used alone
	 * @return
	 */
	@Cacheable(cacheManager =L2CacheConfig.L2CACHE_REMOTE_CACHE_MANAGER,cacheNames = "t123", key = "#id")
	public String getRemoteRedisCache() {
		return "result";
	}
}
