#整合redis与ehcache 做二级缓存，同时添加数据拦截器防止缓存击穿


/**
 * 黑白名单校验注解
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