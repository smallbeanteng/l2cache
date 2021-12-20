package com.atommiddleware.cache.intercept;

import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.atommiddleware.cache.annotation.DataIntercept.CheckType;
import com.atommiddleware.cache.annotation.DataIntercept.DataType;
import com.atommiddleware.cache.core.L2CacheConfig;
import com.google.common.hash.BloomFilter;

public class BloomFilterDataInterceptor implements DataInterceptor {

	private BloomFilter<String> bloomFilterWhite = null;

	private Map<String, String> blackFilter = null;

	private static final String EMPTY = "";

	private int maxBlackList=L2CacheConfig.DEFAULT_MAX_BALCK_LIST;
	
	public BloomFilterDataInterceptor(BloomFilter<String> bloomFilterWhite, Map<String, String> blackFilter, int maxBlackList) {
		this.bloomFilterWhite = bloomFilterWhite;
		this.blackFilter = blackFilter;
		this.maxBlackList=maxBlackList;
	}

	@Override
	public void add(Set<DataMember> arrayDataMember, DataType dataType) throws ArrayIndexOutOfBoundsException,IllegalArgumentException{
		for (DataMember dt : arrayDataMember) {
			add(dt, dataType);
		}
	}

	@Override
	public boolean validData(DataMember dataMember, CheckType ckType) {
		switch (ckType) {
		case BLACK:
			return !blackFilter.containsKey(dataMember.toString());
		case WHITE:
			return bloomFilterWhite.mightContain(dataMember.toString());
		default:
			return bloomFilterWhite.mightContain(dataMember.toString()) && !blackFilter.containsKey(dataMember.toString());
		}
	}

	@Override
	public void add(DataMember dataMember, DataType dataType) throws ArrayIndexOutOfBoundsException,IllegalArgumentException{
		if(!StringUtils.hasText(dataMember.getPrefix())||!StringUtils.hasText(dataMember.getKey())) {
			throw new IllegalArgumentException("dataMember prefix or key empty");
		}
		if (DataType.BLACK.equals(dataType)) {
			if (blackFilter.size()>maxBlackList){
				throw new ArrayIndexOutOfBoundsException("blackList Beyond the boundar");
			}
			blackFilter.put(dataMember.toString(), EMPTY);
		} else {
			bloomFilterWhite.put(dataMember.toString());
		}
	}

	@Override
	public void delBlackData(DataMember dataMember) {
		blackFilter.remove(dataMember.toString());
	}

	@Override
	public void clearBlackData() {
		blackFilter.clear();
	}
}
