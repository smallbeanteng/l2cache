package com.atommiddleware.cache.expression;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
public class CacheExpressionRootObject {
	  private final Method method;

	    private final Object[] args;

	    private final Object target;

	    private final Class<?> targetClass;


	    public CacheExpressionRootObject(Method method, Object[] args, Object target, Class<?> targetClass) {

	        Assert.notNull(method, "Method必传");
	        Assert.notNull(targetClass, "targetClass必传");
	        this.method = method;
	        this.target = target;
	        this.targetClass = targetClass;
	        this.args = args;
	    }

	    public Method getMethod() {
	        return this.method;
	    }

	    public String getMethodName() {
	        return this.method.getName();
	    }

	    public Object[] getArgs() {
	        return this.args;
	    }

	    public Object getTarget() {
	        return this.target;
	    }

	    public Class<?> getTargetClass() {
	        return this.targetClass;
	    }
}
