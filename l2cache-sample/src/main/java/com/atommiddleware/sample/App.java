package com.atommiddleware.sample;

import org.ehcache.core.Ehcache;
import org.ehcache.core.EhcacheManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ConfigurableApplicationContext;

import com.atommiddleware.cache.core.L2CacheConfig;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App {
	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(App.class, args);
		TestCache testCache = applicationContext.getBean(TestCache.class);
		//L2 cache
		System.out.println(testCache.getSample("123"));
		//spring.redis cache
		testCache.getSampleLoacal("678");
		TestDataIntercept testDataIntercept=applicationContext.getBean(TestDataIntercept.class);
		// The data interceptor is used in conjunction with the L2 cache
		testDataIntercept.validateWithCache("1246");
		// Use @Dataintercept alone
		testDataIntercept.validate("1246");
		// Failed data interceptor verification
		testDataIntercept.validate("1345");
	}
}
