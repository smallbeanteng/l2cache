package com.atommiddleware.cache.core;

public class L2CacheContext {
	
	static ThreadLocal<L2CacheContext> localL2cacheContext=new ThreadLocal<L2CacheContext>() {
		protected L2CacheContext initialValue() {
			return new L2CacheContext();
		};
	};

	private L2CacheContext(){
		
	}
	/**
	 * Expiration time in seconds
	 */
	private Long expirationTime;
	public Long getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public static L2CacheContext getContext() {
		return localL2cacheContext.get();
	}
}
