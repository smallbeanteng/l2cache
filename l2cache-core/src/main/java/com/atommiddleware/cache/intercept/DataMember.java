package com.atommiddleware.cache.intercept;

import com.atommiddleware.cache.core.L2CacheConfig;
/**
 * List data members
 * @author ruoshui
 *
 */
public class DataMember {

	public DataMember(String prefix,String key) {
		this.prefix=prefix;
		this.key=key;
	}
	/**
	 * Data prefix
	 */
	private String prefix;
	/**
	 * key
	 */
	private String key;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return prefix.concat(L2CacheConfig.KEY_PREFIX).concat(key);
	}
}
