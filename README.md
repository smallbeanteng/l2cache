# l2cache project #
Ehcache3 and redis are integrated, and a data interceptor is provided to solve cache penetration. At the same time, the secondary cache greatly reduces the possibility of cache avalanche and breakdown. In addition, the redis timeout key local synchronous deletion function is provided to alleviate the problem of data consistency between redis and local cache.

## Getting started ##
The following code snippet comes from l2cache sample. 

## Maven dependency ##
    <properties>
		<guava.version>28.1-jre</guava.version>
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
			<version>1.0.8</version>
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
## Jedis ##
 Jedis replaces the relevant with the following,Jedis replaces the relevant configuration with the following, and the configuration file is changed to the relevant configuration of jedis.

    <dependency>
			<groupId>com.atommiddleware</groupId>
			<artifactId>l2cache-spring-boot-starter</artifactId>
			<version>1.0.8</version>
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

## ehcache ##
Please configure the ehcache XML in the Resources folder

## Invalid data interception ##
Invalid data interception occurs before method execution and caching,There are three verification methods for lists: white list verification, blacklist verification, and white list + blacklist verification,The default is white list verification,If the verification fails, an exception will be thrown

Interceptor usage

    
	@Autowired
	private DataInterceptor dataInterceptor;

	@PostConstruct
	public void init() {
		// WhiteList initialization
		dataInterceptor.add(new DataMember("user", "1246"), DataType.WHITE);
		// Blacklist initialization
		dataInterceptor.add(new DataMember("user", "158"), DataType.BLACK);
	}

	/**
	 * Use @Dataintercept alone
	 * 
	 * @param id
	 */
	@DataIntercept(prefix = "user", key = "#id")
	public void validate(String id) {
		System.out.println("Black and white list verification passed:" + id);
	}

	/**
	 * In combination with @Cacheable, it can also be combined
	 * with @CacheEvict @CachePut. The prefix of @DataIntercept will be equal to the
	 * cacheNames[0] parameter of the cache annotation, and the key will be equal to
	 * the key of the cache annotation
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
## L2 cache usage ##
For example, if you want to find data, you will first find it from ehcache3. If it is not found in ehcache3, you will find it from redis. If it is not found in redis, you will execute the method, and then cache the execution results of the method into ehcache3 and redis and return the results. If there is data in the cache, the method will not be executed.

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

## Configuration description ##
    #Whether to enable Level 2 cache + data interception. The default value is true
    com.atommiddleware.l2cache.enable=true
    #Whether to enable the L2 cache module. The default value is true
    com.atommiddleware.l2cache.cacheConfig.enable=true
    #The remote cache timeout of L2 cache is 300 seconds by default
    com.atommiddleware.l2cache.cacheConfig.defaultTimeout=300
    #Whether to enable redis event listening. No by default. Enable the default registration to listen to the remote cache timeout key.
    #When the remote cache timeout, delete the local
    com.atommiddleware.l2cache.cacheConfig.enableRedisEvent=true
    #The second level cache redis configuration is the same as spring Redis node configuration attributes are similar
    com.atommiddleware.l2cache.cacheConfig.redis.host=0.0.0.0
    com.atommiddleware.l2cache.cacheConfig.redis.port=6379
    com.atommiddleware.l2cache.cacheConfig.redis.password=*
    com.atommiddleware.l2cache.cacheConfig.redis.lettuce.pool.max-active=8
    com.atommiddleware.l2cache.cacheConfig.redis.lettuce.pool.max-wait=-1ms
    com.atommiddleware.l2cache.cacheConfig.redis.lettuce.pool.max-idle=8
    com.atommiddleware.l2cache.cacheConfig.redis.lettuce.pool.min-idle=0

    #Whether to enable the data interception module. The default value is true
    com.atommiddleware.l2cache.dataInterceptor.enable=true
    #Expected data volume of white list bloom filter, 100000 by default
    com.atommiddleware.l2cache.dataInterceptor.expectedInsertions=100000
    #Fault tolerance rate of white list bloom filter, default 0.01
    com.atommiddleware.l2cache.dataInterceptor.fpp=0.01
    #The blacklist is limited in length. If it is added beyond the limit, exceptions will be thrown. The default value is 100000
    com.atommiddleware.l2cache.dataInterceptor.maxBlackList=100000

    #spring.redis The default configuration does not interfere with L2 cache
    spring.redis.host=127.0.0.1
    spring.redis.port=6379
    spring.redis.password=
    spring.redis.pool.max-active=8
    spring.redis.pool.max-wait=-1
    spring.redis.pool.max-idle=8
    spring.redis.pool.min-idle=0
    spring.redis.timeout=30000

## Other instructions ##
If you want to use ehcache3 or redis alone, please configure the explicit cache manager name on the annotation

    //ehcache3 cacheManager Name
    //L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER
    //l2cache remote redis cacheManager Name
    //L2CacheConfig.L2CACHE_REMOTE_CACHE_MANAGER

    	@Cacheable(cacheManager =L2CacheConfig.L2CACHE_LOCAL_CACHE_MANAGER,cacheNames = "t123", key = "#id")
	    public String getEhcache3Cache(String id) {
		return "result";
	    }
	
	    @Cacheable(cacheManager =L2CacheConfig.L2CACHE_REMOTE_CACHE_MANAGER,cacheNames = "t123", key = "#id")
	    public String getRemoteRedisCache(String id) {
		return "result";
	    }
