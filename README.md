# l2cache project #
Ehcache3 and redis are integrated, and a data interceptor is provided to solve cache penetration. At the same time, the secondary cache greatly reduces the possibility of cache avalanche and breakdown. In addition, the redis timeout key local synchronous deletion function is provided to alleviate the problem of data consistency between redis and local cache

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
 Jedis replaces the relevant with the following,Jedis replaces the relevant configuration with the following, and the configuration file is changed to the relevant configuration of jedis

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