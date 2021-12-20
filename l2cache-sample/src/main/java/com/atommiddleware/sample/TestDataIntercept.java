package com.atommiddleware.sample;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.atommiddleware.cache.annotation.DataIntercept;
import com.atommiddleware.cache.annotation.DataIntercept.DataType;
import com.atommiddleware.cache.intercept.DataInterceptor;
import com.atommiddleware.cache.intercept.DataMember;
/**
 * Test DataInterceptor
 * @author ruoshui
 *
 */
@Component
public class TestDataIntercept {
	@Autowired
	private DataInterceptor dataInterceptor;

	@PostConstruct
	public void init() {
		// WhiteList initialization
		dataInterceptor.add(new DataMember("user", "1246"), DataType.WHITE);
		// Blacklist initialization
		dataInterceptor.add(new DataMember("user", "158"), DataType.BLACK);
	}

	/**
	 * Use @Dataintercept alone
	 * 
	 * @param id
	 */
	@DataIntercept(prefix = "user", key = "#id")
	public void validate(String id) {
		System.out.println("Black and white list verification passed:" + id);
	}

	/**
	 * In combination with @Cacheable, it can also be combined
	 * with @CacheEvict @CachePut. The prefix of @DataIntercept will be equal to the
	 * cacheNames[0] parameter of the cache annotation, and the key will be equal to
	 * the key of the cache annotation
	 * 
	 * @param id
	 */
	@DataIntercept
	@Cacheable(cacheNames = "user", key = "#id")
	public String validateWithCache(String id) {
		System.out.println("Black and white list verification passed:" + id);
		return null;
	}
}
