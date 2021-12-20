package com.atommiddleware.cache.core;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * l2cache configuration
 * @author ruoshui
 *
 */
@ConfigurationProperties(prefix = "com.atommiddleware.l2cache")
public class L2CacheConfig {

	/**
	 * Default limit quantity of blacklist
	 */
	public static final int DEFAULT_MAX_BALCK_LIST = 100000;
	/**
	 * Expected amount of data stored in bloom filter
	 */
	public static final int DEFAULT_EXPECTEDINSERTIONS = 100000;
	/**
	 * Fault tolerance of Bloom filter
	 */
	public static final double DEFAULT_FPP = 0.01;
	/**
	 * Remote cache default timeout in seconds
	 */
	public static final int DEFAULT_REMOTE_TIMEOUT = 300;
	/**
	 * Separator between cache name and key
	 */
	public final static String KEY_PREFIX="::";
	/**
	 * Default processor for remote cache expiration messages
	 */
	public static final String L2CACHE_HANDLE_EXPIRE_KEY="l2CacheHandleExpireKey";
	/**
	 * Remote cache
	 */
	public static final String L2CACHE_REMOTE_CACHE_MANAGER="l2CacheRemoteCacheManager";
	/**
	 * Local cache
	 */
	public static final String L2CACHE_LOCAL_CACHE_MANAGER="l2CacheLocalCacheManager";
	/**
	 * L2 cache object
	 */
	public static final String L2CACHE_CACHE_MANAGER="l2CacheManager";
	/**
	 * Remote cache connection
	 */
	public static final String L2CACHE_REMOTE_REDIS_CONNECTION_FACTORY="l2CacheRemoteRedisConnectionFactory";
	/**
	 * remote cache configuration
	 */
	public static final String  L2CACHE_REMOTE_REDIS_CONFIGURATION="l2CacheRemoteRedisConfiguration";
	/**
	 * Enable L2 cache + blacklist interception
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
		 * Whether to enable the L2 cache module. It is enabled by default
		 */
		private boolean enable;
		/**
		 * Remote cache timeout for L2 cache
		 */
		private int defaultTimeout;
		/**
		 * Enable event listening? No by default. If enabled, the remote cache timeout key will be registered by default
		 */
		private boolean enableRedisEvent;
		/**
		 * Redis configuration
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
		 * Whether to enable black-and-white list interception. It is enabled by default
		 */
		private boolean enable = true;
		/**
		 * Expected data volume of white list bloom filter
		 */
		private int expectedInsertions;
		/**
		 * Fault tolerance of white list bloom filter
		 */
		private double fpp;
		/**
		 *  Blacklist length limit. Adding beyond the limit will throw exceptions
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
