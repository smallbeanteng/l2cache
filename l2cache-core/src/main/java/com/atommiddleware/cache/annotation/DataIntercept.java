package com.atommiddleware.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 黑白名单校验
 * @author ruoshui
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataIntercept {
	/**
	 * key 支持spel表达式
	 * 
	 * @return spel表达式
	 */
	String key() default "";

	/**
	 * 业务前缀用于区分不同业务的数据
	 * 
	 * @return
	 */
	String prefix() default "";

	/**
	 * 开启自动发现key,如果key 没有值会去标注在同一个方法上的@Cacheable @CacheEvict @CachePut 上去找key 及prefix 会取cacheNames的第一个
	 * 
	 * @return 是否开启自动发现key 默认开启
	 */
	boolean enalbleAutoDiscoveryKey() default true;

	/**
	 * 条件表达式
	 * 
	 * @return 条件表达式
	 */
	String condition() default "";

	/**
	 * 校验模式 白名单中存在，或 黑名单中不存在，或两者都要白名单存在，并且黑名单中不存在
	 * 
	 * @return 校验模式 默认白名单校验
	 */
	CheckType ckType() default CheckType.WHITE;

	public enum CheckType {
		/**
		 * 黑名单校验(在黑名单中的数据不通过)
		 */
		BLACK,
		/**
		 * 白名单校验(不在白名单中的数据不通过)
		 */
		WHITE,
		/**
		 * 黑白名单都校验(白名单通过，并且黑名单校验通过)
		 */
		BLACK_AND_WHITE
	}

	/**
	 * 数据种类
	 * 
	 * @author ruoshui
	 *
	 */
	public enum DataType {
		/**
		 * 黑名单数据
		 */
		BLACK,
		/**
		 * 白名单数据
		 */
		WHITE
	}
}
