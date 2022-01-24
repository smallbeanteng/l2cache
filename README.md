# l2cache 开源项目 #
ehcache与redis集成，提供数据拦截器解决缓存穿透问题。同时，二级缓存大大降低了缓存雪崩和击穿的可能性。此外，还提供了redis超时键本地同步删除功能，缓解了redis与本地缓存的数据一致性问题。

## 开源项目github ##
    https://github.com/smallbeanteng/l2cache

## 项目开始 ##
具体看使用例子。 

## Maven dependency ##
    <properties>
		<guava.version>31.0.1-jre</guava.version>
		<ehcache.version>3.9.7</ehcache.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>com.atommiddleware</groupId>
			<artifactId>l2cache-spring-boot-starter</artifactId>
			<version>1.0.9</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-pool2</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>
		<dependency>
			<groupId>org.aspectj</groupId>
			<artifactId>aspectjweaver</artifactId>
		</dependency>
		<dependency>
			<groupId>com.google.guava</groupId>
			<artifactId>guava</artifactId>
			<version>${guava.version}</version>
		</dependency>
		<dependency>
			<groupId>org.ehcache</groupId>
			<artifactId>ehcache</artifactId>
		</dependency>
	</dependencies>
## 使用 Jedis  ##
 jedis将相关配置替换为以下内容，配置文件更改为jedis的相关配置即可

    <dependency>
			<groupId>com.atommiddleware</groupId>
			<artifactId>l2cache-spring-boot-starter</artifactId>
			<version>1.0.9</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
			<exclusions>
				<exclusion>
					<groupId>io.lettuce</groupId>
					<artifactId>lettuce-core</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>redis.clients</groupId>
			<artifactId>jedis</artifactId>
	</dependency>

## ehcache 配置 ##
请在Resources文件夹中配置ehcache XML

## Invalid data interception ##
方法执行和缓存前会发生无效数据拦截，列表有三种验证方法：白名单验证、黑名单验证、白名单+黑名单验证，默认为白名单验证，如果验证失败，会抛出异常

拦截器使用如下

    
	@Autowired
	private DataInterceptor dataInterceptor;

	@PostConstruct
	public void init() {
		// 白名单数据初始化
		dataInterceptor.add(new DataMember("user", "1246"), DataType.WHITE);
		// 黑名单数据初始化
		dataInterceptor.add(new DataMember("user", "158"), DataType.BLACK);
	}

	/**
	 * @Dataintercept 单独使用
	 * 
	 * @param id
	 */
	@DataIntercept(prefix = "user", key = "#id")
	public void validate(String id) {
		System.out.println("Black and white list verification passed:" + id);
	}

	/**
	 * 和 @Cacheable一起使用, 它能自动找到相关配置拦截器注解@Dataintercept的prefix与key会找到
	 * @Cacheable、@CacheEvict、@CachePut 注解的cacheNames[0]和key,并与它们保持一致
	 * 
	 * @param id
	 */
	@DataIntercept
	@Cacheable(cacheNames = "user", key = "#id")
	public String validateWithCache(String id) {
		System.out.println("Black and white list verification passed:" + id);
		return null;
	}
```

    // The data interceptor is used in conjunction with the L2 cache
       validateWithCache("1246");
    // Use @Dataintercept alone
       validate("1246");
    // Failed data interceptor verification,Because this list is not on our list
       validate("1345");
```
## 二级缓存 使用 ##
例如，如果要查找数据，首先从ehcache中查找数据。如果在ehcache中找不到它，将在redis中找到它。如果在redis中找不到，则执行方法，然后将方法的执行结果缓存到ehcache和redis中并返回结果。如果缓存中有数据，则不会执行该方法。

    	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * The cache cacheNames configured with l2cache are in ehCache Configuration in XML
	 * 
	 * @param id
	 * @return
	 */
	@Cacheable(cacheNames = "t123", key = "#id")
	public String getSample(String id) {
		System.out.println("id The obtained results will be cached in L2:" + id);
		return String.valueOf(ThreadLocalRandom.current().nextInt(1000));
	}
	
	/**
	 * Using spring Redis configured cache
	 * @param id
	 * @return
	 */
	public String getSampleDefaultRedisConfiguration(String id) {
		System.out.println("Using spring Redis configuration cache");
		stringRedisTemplate.opsForValue().set(id, id);
		return stringRedisTemplate.opsForValue().get(id);
	}
```

    //L2 cache
    getSample("123");
    //spring.redis default redis configuration cache
    getSampleDefaultRedisConfiguration("678");
```
## 配置文件说明 ##
    #是否开启二级缓存整个项目组件，默认为true
    com.atommiddleware.l2cache.enable=true
    #是否开启二级缓存模块，默认为true
    com.atommiddleware.l2cache.cacheConfig.enable=true
    #二级缓存的远程[redis],默认缓存时间300s
    com.atommiddleware.l2cache.cacheConfig.defaultTimeout=300
    #是否开启监听redis key 过期事件并同步删除本地key,保证redis与本地的数据相对一致性，默认false
    com.atommiddleware.l2cache.cacheConfig.enableRedisEvent=false
    #二级缓存的redis 配置与spring.redis 配置一样只不过前缀不同
    com.atommiddleware.l2cache.cacheConfig.redis.host=0.0.0.0
    com.atommiddleware.l2cache.cacheConfig.redis.port=6379
    com.atommiddleware.l2cache.cacheConfig.redis.password=*
    com.atommiddleware.l2cache.cacheConfig.redis.lettuce.pool.max-active=8
    com.atommiddleware.l2cache.cacheConfig.redis.lettuce.pool.max-wait=-1ms
    com.atommiddleware.l2cache.cacheConfig.redis.lettuce.pool.max-idle=8
    com.atommiddleware.l2cache.cacheConfig.redis.lettuce.pool.min-idle=0

    #是否开启数据拦截模块，默认true
    com.atommiddleware.l2cache.dataInterceptor.enable=true
    #预期布隆过滤器数据量,默认100000
    com.atommiddleware.l2cache.dataInterceptor.expectedInsertions=100000
    #预期布隆过滤器容错率，默认0.01
    com.atommiddleware.l2cache.dataInterceptor.fpp=0.01
    #黑名单数据容量最大值，默认100000
    com.atommiddleware.l2cache.dataInterceptor.maxBlackList=100000

    #spring.redis 的配置，与二级缓存的配置互不干扰
    spring.redis.host=127.0.0.1
    spring.redis.port=6379
    spring.redis.password=
    spring.redis.pool.max-active=8
    spring.redis.pool.max-wait=-1
    spring.redis.pool.max-idle=8
    spring.redis.pool.min-idle=0
    spring.redis.timeout=30000

## Other instructions ##
如果你想单独使用ehcache或者redis 值需要标明要使用cacheManager就可以了

    //ehcache的cacheManager名字
    //L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER
    //redis的cacheManager的名字
    //L2CacheConfig.L2CACHE_REMOTE_CACHE_MANAGER

    	@Cacheable(cacheManager =L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER,cacheNames = "t123", key = "#id")
	    public String getEhcache3Cache(String id) {
		return "result";
	    }
	
	    @Cacheable(cacheManager =L2CacheConfig.L2CACHE_REMOTE_CACHE_MANAGER,cacheNames = "t123", key = "#id")
	    public String getRemoteRedisCache(String id) {
		return "result";
	    }
