package com.didiglobal.sample.test;

import com.didiglobal.jushata.springboot.JushataApplication;
import com.didiglobal.sample.api.SampleBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPathProvider;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@SpringBootApplication
public class JushataApplicationSampleTest {

    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext ctx = JushataApplication.run(JushataApplicationSampleTest.class, args);

        System.out.println(ctx);
        System.out.println("id:\n " + ctx.getId());
        System.out.println("name:\n " + ctx.getApplicationName());
        System.out.println("parent:\n " + ctx.getParent());
        System.out.println("classloader:\n " + ctx.getClassLoader());
        System.out.println("parent classloader:\n " + ctx.getClassLoader().getParent());
        System.out.println("spring.application.name:\n " + ctx.getEnvironment().getProperty("spring.application.name"));
        System.out.println("jushata.modules-file:\n " + ctx.getEnvironment().getProperty("jushata.modules-file"));

        printBeans(ctx, SampleBean.class);
        printBeans(ctx, DispatcherServlet.class);
        printBeans(ctx, DispatcherServletPathProvider.class);
        printBeans(ctx, RequestMappingHandlerAdapter.class);
        printBeans(ctx, RequestMappingHandlerMapping.class);
    }

    static void printBeans(ConfigurableApplicationContext ctx, Class type) {
        try {
            System.out.println(type.getName() + ": [");
            Map<String, Object> beans = ctx.getBeansOfType(type);
            for (String beanName : beans.keySet()) {
                System.out.println(" " + beanName + " : " + beans.get(beanName));
            }
            if (beans.isEmpty()) {
                System.out.println(ctx.getBean(type));
            }
            System.out.println("]");
        } catch (Exception e) {
        }
    }
}
