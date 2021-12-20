package com.atommiddleware.cache.intercept;

import java.util.Set;

import com.atommiddleware.cache.annotation.DataIntercept.CheckType;
import com.atommiddleware.cache.annotation.DataIntercept.DataType;
/**
 * Data interception interface
 * @author ruoshui
 *
 */
public interface DataInterceptor {
	/**
	 * Add data to the list
	 * @param arrayDataMember Data to add
	 * @param dataType To which list is the data added
	 */
	void add(Set<DataMember> arrayDataMember, DataType dataType) throws ArrayIndexOutOfBoundsException,IllegalArgumentException;

	/**
	 * Add data to the list
	 * @param dataMember data
	 * @param type To which list is the data added
	 */
	void add(DataMember dataMember, DataType type) throws ArrayIndexOutOfBoundsException,IllegalArgumentException;

	/**
	 * Check whether the data is passed
	 * 
	 * @param dataMember    Data to be verified
	 * @param ckType Verification method
	 * @return Whether it passes the verification
	 */
	boolean validData(DataMember dataMember, CheckType ckType);

	/**
	 * Delete specified data from blacklist
	 * 
	 * @param dataMember data
	 */
	void delBlackData(DataMember dataMember);

	/**
	 * Clear blacklist data
	 */
	void clearBlackData();
}
