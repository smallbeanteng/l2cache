package com.atommiddleware.cache.annotation;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;

import com.atommiddleware.cache.expression.CacheOperationExpressionEvaluator;
import com.atommiddleware.cache.intercept.DataInterceptor;
import com.atommiddleware.cache.intercept.DataMember;
/**
 * DataInterceptAop
 * @author ruoshui
 *
 */
@Aspect
public class DataInterceptAop implements Ordered {

	public static final String L2_CACHE_DATA_INTERCEPT_AOP = "com.atommiddleware.cache.annotation.DataInterceptAop";

	private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();

	private SingletonSupplier<KeyGenerator> keyGenerator = SingletonSupplier.of(SimpleKeyGenerator::new);

	@Autowired(required = false)
	private DataInterceptor dataInterceptor;

	@Pointcut("@annotation(com.atommiddleware.cache.annotation.DataIntercept)")
	public void cutDataIntercept() {

	}

	@Around("cutDataIntercept()")
	public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		return execute(joinPoint);
	}

	private Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
		Class<?> targetClass = AopProxyUtils.ultimateTargetClass(joinPoint.getTarget());
		if (joinPoint.getSignature() instanceof MethodSignature && null != dataInterceptor) {
			// Get method
			Method method = getSpecificMethod(((MethodSignature) joinPoint.getSignature()).getMethod(), targetClass);
			executeDataIntercept(method, joinPoint.getArgs(), joinPoint.getTarget(), targetClass);
		}
		return joinPoint.proceed();
	}

	private void executeDataIntercept(Method method, Object[] args, Object target, Class<?> targetClass) {
		DataIntercept dataInterceptKey = AnnotationUtils.findAnnotation(method, DataIntercept.class);
		// Verification conditions
		boolean isPassing = isConditionPassing(dataInterceptKey, method, args, target, targetClass);
		if (!isPassing) {
			throw new IllegalArgumentException("argument condition valid fail");
		}
		DataInterceptPrefixKeySpel dataInterceptPrefixKey = findKey(method, dataInterceptKey);
		Object key = generateKey(dataInterceptPrefixKey.getKey(), method, args, target, targetClass);
		if (null != key) {
			boolean result = dataInterceptor.validData(
					new DataMember(dataInterceptPrefixKey.getPrefix(), String.valueOf(key)), dataInterceptKey.ckType());
			if (!result) {
				throw new IllegalArgumentException(
						String.format("argument valid fail checkType [%s]", dataInterceptKey.ckType().name()));
			}
		}
	}

	private boolean isConditionPassing(DataIntercept dataInterceptKey, Method method, Object[] args, Object target,
			Class<?> targetClass) {
		if (StringUtils.hasText(dataInterceptKey.condition())) {
			EvaluationContext evaluationContext = evaluator.createEvaluationContext(method, args, target, targetClass,
					CacheOperationExpressionEvaluator.NO_RESULT);
			AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);
			return evaluator.condition(dataInterceptKey.condition(), methodCacheKey, evaluationContext);
		}
		return true;
	}

	private DataInterceptPrefixKeySpel findKey(Method method, DataIntercept dataInterceptKey) {
		if (StringUtils.hasText(dataInterceptKey.key()) && StringUtils.hasText(dataInterceptKey.prefix())) {
			return new DataInterceptPrefixKeySpel(dataInterceptKey.prefix(), dataInterceptKey.key());
		}
		if (dataInterceptKey.enalbleAutoDiscoveryKey()) {
			Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
			if (StringUtils.hasText(cacheable.key())) {
				return new DataInterceptPrefixKeySpel(cacheable.cacheNames()[0], cacheable.key());
			}
			CachePut cachePut = AnnotationUtils.findAnnotation(method, CachePut.class);
			if (StringUtils.hasText(cachePut.key())) {
				return new DataInterceptPrefixKeySpel(cachePut.cacheNames()[0], cachePut.key());
			}
			CacheEvict cacheEvict = AnnotationUtils.findAnnotation(method, CacheEvict.class);
			if (StringUtils.hasText(cacheEvict.key())) {
				return new DataInterceptPrefixKeySpel(cacheEvict.cacheNames()[0], cacheEvict.key());
			}
		}
		throw new IllegalArgumentException("DataIntercept param fail");
	}

	private Object generateKey(String keySpEl, Method method, Object[] args, Object target, Class<?> targetClass) {
		if (StringUtils.hasText(keySpEl)) {
			EvaluationContext evaluationContext = evaluator.createEvaluationContext(method, args, target, targetClass,
					CacheOperationExpressionEvaluator.NO_RESULT);
			AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);
			Object keyValue = evaluator.key(keySpEl, methodCacheKey, evaluationContext);
			return keyValue;
		}
		return this.keyGenerator.get().generate(target, method, args);
	}

	private Method getSpecificMethod(Method method, Class<?> targetClass) {
		return AopUtils.getMostSpecificMethod(method, targetClass);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
/**
 * DataInterceptPrefixKeySpel
 * @author ruoshui
 *
 */
	public class DataInterceptPrefixKeySpel {

		public DataInterceptPrefixKeySpel(String prefix, String key) {
			this.prefix = prefix;
			this.key = key;
		}

		private String prefix;

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
	}
}
