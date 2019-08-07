package com.github.ayltai.hknews;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class LazyInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(@NonNull @lombok.NonNull final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (final String name : beanFactory.getBeanDefinitionNames()) beanFactory.getBeanDefinition(name).setLazyInit(true);
    }
}
