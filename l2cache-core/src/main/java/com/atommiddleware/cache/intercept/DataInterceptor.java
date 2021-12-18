package com.atommiddleware.cache.intercept;

import java.util.Set;

import com.atommiddleware.cache.annotation.DataIntercept.CheckType;
import com.atommiddleware.cache.annotation.DataIntercept.DataType;
/**
 * 数据拦截接口
 * @author ruoshui
 *
 */
public interface DataInterceptor {
	/**
	 * 向名单中添加数据
	 * @param arrayDataMember 要添加的数据
	 * @param dataType 数据添加到哪个名单中
	 */
	void add(Set<DataMember> arrayDataMember, DataType dataType) throws ArrayIndexOutOfBoundsException,IllegalArgumentException;

	/**
	 * 向名单中添加数据
	 * @param dataMember 数据
	 * @param type 数据添加到哪个名单中
	 */
	void add(DataMember dataMember, DataType type) throws ArrayIndexOutOfBoundsException,IllegalArgumentException;

	/**
	 * 校验数据是否通过
	 * 
	 * @param dataMember    要校验的数据
	 * @param ckType 校验方式
	 * @return 是否通过校验
	 */
	boolean validData(DataMember dataMember, CheckType ckType);

	/**
	 * 从黑名单中删除指定数据
	 * 
	 * @param dataMember 数据
	 */
	void delBlackData(DataMember dataMember);

	/**
	 * 清空黑名单数据
	 */
	void clearBlackData();
}
