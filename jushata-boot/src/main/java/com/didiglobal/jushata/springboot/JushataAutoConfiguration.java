package com.didiglobal.jushata.springboot;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

public class JushataAutoConfiguration {

    @Configuration
    @ConditionalOnWebApplication
    @AutoConfigureAfter(ServletWebServerFactoryAutoConfiguration.class)
    protected static class DispatcherServletConfiguration {

        @Bean
        public DispatcherServlet dispatcherServlet(WebMvcProperties webMvcProperties) {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setDispatchOptionsRequest(webMvcProperties.isDispatchOptionsRequest());
            dispatcherServlet.setDispatchTraceRequest(webMvcProperties.isDispatchTraceRequest());
            dispatcherServlet.setThrowExceptionIfNoHandlerFound(webMvcProperties.isThrowExceptionIfNoHandlerFound());
            return dispatcherServlet;
        }
    }

    @Configuration
    @ConditionalOnWebApplication
    @AutoConfigureAfter({ DispatcherServletConfiguration.class, ValidationAutoConfiguration.class })
    protected static class EnableWebMvcConfiguration extends WebMvcAutoConfiguration.EnableWebMvcConfiguration {

        public EnableWebMvcConfiguration(ObjectProvider<WebMvcProperties> mvcPropertiesProvider,
                                         ObjectProvider<WebMvcRegistrations> mvcRegistrationsProvider,
                                         ListableBeanFactory beanFactory) {
            super(mvcPropertiesProvider, mvcRegistrationsProvider, beanFactory);
        }
    }

}
