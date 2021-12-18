package com.atommiddleware.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.netty.util.internal.ThreadLocalRandom;

@Component
public class TestCache {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 使用l2cache配置的缓存 cacheNames 在ehcache.xml中配置
	 * 
	 * @param id
	 * @return
	 */
	@Cacheable(cacheNames = "t123", key = "#id")
	public String getSample(String id) {
		System.out.println("第一进入:" + id);
		return String.valueOf(ThreadLocalRandom.current().nextInt(1000));
	}
	
	/**
	 * 使用spring.redis 配置的缓存
	 * 
	 * @param id
	 * @return
	 */
	public String getSampleLoacal(String id) {
		System.out.println("使用了spring.redis 的配置的缓存");
		stringRedisTemplate.opsForValue().set(id, id);
		return String.valueOf(ThreadLocalRandom.current().nextInt(1000));
	}
}
