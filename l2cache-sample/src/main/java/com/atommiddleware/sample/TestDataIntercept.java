package com.atommiddleware.sample;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.atommiddleware.cache.annotation.DataIntercept;
import com.atommiddleware.cache.annotation.DataIntercept.DataType;
import com.atommiddleware.cache.intercept.DataInterceptor;
import com.atommiddleware.cache.intercept.DataMember;

@Component
public class TestDataIntercept {
	@Autowired
	private DataInterceptor dataInterceptor;

	@PostConstruct
	public void init() {
		dataInterceptor.add(new DataMember("user", "1246"), DataType.WHITE);
		dataInterceptor.add(new DataMember("user", "158"), DataType.BLACK);
	}

	@DataIntercept(prefix = "user", key = "#id")
	public void validate(String id) {
		System.out.println("穿过了" + id);
	}

	/**
	 * 可以跟随@Cacheable的配置 cacheNames[0]=prefix key=key
	 * 
	 * @param id
	 */
	@DataIntercept
	@Cacheable(cacheNames = "user", key = "#id")
	public void validateWithCache(String id) {
		System.out.println("穿过了" + id);
	}
}
