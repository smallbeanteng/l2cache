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
	 * Key supports spel expressions
	 * @return Key supports spel expressions
	 */
	String key() default "";

	/**
	 * The service prefix is used to distinguish the data of different services
	 * @return The service prefix is used to distinguish the data of different services
	 */
	String prefix() default "";

	/**
	 *  Turn on automatic key discovery. If the key has no value,
	 *  it will be marked on @Cacheable @CacheEvict @CachePut on the same method to find the key and prefix, and the first cachenames will be taken
	 * @return Turn on automatic key discovery
	 */
	boolean enalbleAutoDiscoveryKey() default true;

	/**
	 * Conditional expression
	 * @return Conditional expression
	 */
	String condition() default "";

	/**
	 * The verification mode exists in the white list, does not exist in the blacklist, or both must exist in the white list and does not exist in the blacklist
	 * @return verification mode
	 */
	CheckType ckType() default CheckType.WHITE;

	public enum CheckType {
		/**
		 * Blacklist verification (data in the blacklist fails)
		 */
		BLACK,
		/**
		 * White list verification (data not in the white list will not pass)
		 */
		WHITE,
		/**
		 * Both black and white lists are verified (the white list passes and the black list passes the verification)
		 */
		BLACK_AND_WHITE
	}

	/**
	 * Data type
	 * 
	 * @author ruoshui
	 *
	 */
	public enum DataType {
		/**
		 * Blacklist data
		 */
		BLACK,
		/**
		 * Whitelist data
		 */
		WHITE
	}
}
