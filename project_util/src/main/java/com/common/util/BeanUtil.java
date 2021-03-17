package com.common.util;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class BeanUtil {

    /**
     * 서비스 클래스의 instance 를 생성한다.
     *
     * @param serivceClass
     *            서비스 클래스의 Class.
     * @return
     * @throws TmallException
     */
    public static <T> T createService(Class<T> serviceClass) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        if (applicationContext == null) {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            return context.getAutowireCapableBeanFactory().getBean(serviceClass);
        } else {
            return applicationContext.getAutowireCapableBeanFactory().getBean(serviceClass);
        }
    }

    public static <T> T getInstance(Class<T> targetClass) throws Exception {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        if (applicationContext == null) {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            return context.getAutowireCapableBeanFactory().getBean(targetClass);
        } else {
            return applicationContext.getAutowireCapableBeanFactory().getBean(targetClass);
        }
    }

    public static Object getBean(String beanName) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        if (applicationContext == null) {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            return context.getBean(beanName);
        } else {
            return applicationContext.getBean(beanName);
        }
    }
}
