package com.atommiddleware.autoconfigure.cache;

import com.atommiddleware.cache.annotation.DataInterceptAop;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
/**
 * Register data interceptor
 * @author ruoshui
 *
 */
public class L2CacheDataInterceptorImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataInterceptAop.class);
        registry.registerBeanDefinition(DataInterceptAop.L2_CACHE_DATA_INTERCEPT_AOP,
                beanDefinitionBuilder.getBeanDefinition());
    }
}
