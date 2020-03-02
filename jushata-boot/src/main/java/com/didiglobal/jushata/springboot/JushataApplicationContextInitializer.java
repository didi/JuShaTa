package com.didiglobal.jushata.springboot;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class JushataApplicationContextInitializer implements ApplicationContextInitializer {

    private ApplicationContext parent;
    private int                httpPort;

    public JushataApplicationContextInitializer(ApplicationContext parent, int httpPort) {
        this.parent = parent;
        this.httpPort = httpPort;
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {

        ConfigurableEnvironment env = ctx.getEnvironment();
        ctx.setParent(this.parent);
        ctx.setId(env.getProperty("spring.application.id", ctx.getClassLoader().toString()));

        if (ctx instanceof AnnotationConfigServletWebServerApplicationContext) {
            final String port = env.getProperty("server.port", String.valueOf(httpPort));
            ctx.getBeanFactory().registerSingleton("webServerFactoryCustomizer",
                (WebServerFactoryCustomizer<ConfigurableWebServerFactory>) factory -> factory
                    .setPort(Integer.valueOf(port)));
        }
    }

}
