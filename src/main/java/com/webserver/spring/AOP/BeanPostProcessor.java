package com.webserver.spring.AOP;

/**
 * @auther wy
 * @create 2022/4/4 14:20
 */
public interface BeanPostProcessor {
    public Object postProcessorBeforeInitializingBean(String beanName, Object bean);
    public Object postProcessorAfterInitializingBean(String beanName, Object bean);
}
