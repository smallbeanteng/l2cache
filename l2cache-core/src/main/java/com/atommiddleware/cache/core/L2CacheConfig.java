package com.atommiddleware.cache.core;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.atommiddleware.l2cache")
public class L2CacheConfig {

	/**
	 * 黑名单默认限制数量
	 */
	public static final int DEFAULT_MAX_BALCK_LIST = 100000;
	/**
	 * 布隆过滤器预期存的数据量
	 */
	public static final int DEFAULT_EXPECTEDINSERTIONS = 100000;
	/**
	 * 布隆过滤器容错率
	 */
	public static final double DEFAULT_FPP = 0.01;
	/**
	 * 远程缓存默认超时时间 单位秒
	 */
	public static final int DEFAULT_REMOTE_TIMEOUT = 300;
	/**
	 * 缓存名与key的分隔符号
	 */
	public final static String KEY_PREFIX="::";
	/**
	 * 远程缓存过期消息默认处理器
	 */
	public static final String L2CACHE_HANDLE_EXPIRE_KEY="l2CacheHandleExpireKey";
	/**
	 * 远程缓存
	 */
	public static final String L2CACHE_REMOTE_CACHE_MANAGER="l2CacheRemoteCacheManager";
	/**
	 * 本地缓存
	 */
	public static final String L2CACHE_LOCAL_CACHE_MANAGER="l2CacheLocalCacheManager";
	/**
	 * 二级缓存对象
	 */
	public static final String L2CACHE_CACHE_MANAGER="l2CacheManager";
	/**
	 * 远程连接
	 */
	public static final String L2CACHE_REMOTE_REDIS_CONNECTION_FACTORY="l2CacheRemoteRedisConnectionFactory";
	
	/**
	 * 是否开启二级缓存+黑白名单拦截
	 */
	private boolean enable;
	private CacheConfig cacheConfig = new CacheConfig();
	private DataInterceptConfig dataInterceptor = new DataInterceptConfig();

	public CacheConfig getCacheConfig() {
		return cacheConfig;
	}

	public void setCacheConfig(CacheConfig cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public DataInterceptConfig getDataInterceptor() {
		return dataInterceptor;
	}

	public void setDataInterceptor(DataInterceptConfig dataInterceptor) {
		this.dataInterceptor = dataInterceptor;
	}

	public class CacheConfig {
		/**
		 * 是否开启二级缓存模块 默认开启
		 */
		private boolean enable;
		/**
		 * 二级缓存之远程缓存超时时间
		 */
		private int defaultTimeout;
		/**
		 * 是否开启redis事件监听 默认否，开启的话会默认注册监听远程缓存超时key
		 */
		private boolean enableRedisEvent;
		/**
		 * redis 配置
		 */
		private RedisProperties redis = new RedisProperties();

		public boolean isEnableRedisEvent() {
			return enableRedisEvent;
		}

		public void setEnableRedisEvent(boolean enableRedisEvent) {
			this.enableRedisEvent = enableRedisEvent;
		}

		public int getDefaultTimeout() {
			return defaultTimeout;
		}

		public void setDefaultTimeout(int defaultTimeout) {
			this.defaultTimeout = defaultTimeout;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public RedisProperties getRedis() {
			return redis;
		}

		public void setRedis(RedisProperties redis) {
			this.redis = redis;
		}

	}

	public class DataInterceptConfig {
		/**
		 * 是否开启黑白名单拦截，默认开启
		 */
		private boolean enable = true;
		/**
		 * 白名单布隆过滤器预期数据量
		 */
		private int expectedInsertions;
		/**
		 * 白名单布隆过滤器容错率
		 */
		private double fpp;
		/**
		 *  黑名单长度限制，超出限制再添加会抛异常
		 */
		private int maxBlackList;

		public int getMaxBlackList() {
			return maxBlackList;
		}

		public void setMaxBlackList(int maxBlackList) {
			this.maxBlackList = maxBlackList;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public int getExpectedInsertions() {
			return expectedInsertions;
		}

		public void setExpectedInsertions(int expectedInsertions) {
			this.expectedInsertions = expectedInsertions;
		}

		public double getFpp() {
			return fpp;
		}

		public void setFpp(double fpp) {
			this.fpp = fpp;
		}
	}
}
