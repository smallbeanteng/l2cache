package com.atommiddleware.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App {
	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(App.class, args);
		TestCache testCache = applicationContext.getBean(TestCache.class);
		//二级缓存
		System.out.println(testCache.getSample("123"));
		System.out.println(testCache.getSample("123"));
		//spring.redis 缓存
		testCache.getSampleLoacal("678");
		// 数据拦截
		TestDataIntercept testDataIntercept=applicationContext.getBean(TestDataIntercept.class);
		// 穿过
		testDataIntercept.validate("1246");
		// 穿过
		testDataIntercept.validateWithCache("1246");
		// 没穿过
		testDataIntercept.validate("1345");
	}
}
