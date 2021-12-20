package com.atommiddleware.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.netty.util.internal.ThreadLocalRandom;
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
	public String getSampleLoacal(String id) {
		System.out.println("Using spring Redis configuration cache");
		stringRedisTemplate.opsForValue().set(id, id);
		return String.valueOf(ThreadLocalRandom.current().nextInt(1000));
	}
}
