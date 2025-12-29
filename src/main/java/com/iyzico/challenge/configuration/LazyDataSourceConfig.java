package com.iyzico.challenge.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class LazyDataSourceConfig implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("dataSource".equals(beanName) && bean instanceof DataSource) {
            if (bean instanceof LazyConnectionDataSourceProxy) {
                return bean;
            }
            return new LazyConnectionDataSourceProxy((DataSource) bean);
        }
        return bean;
    }
}
