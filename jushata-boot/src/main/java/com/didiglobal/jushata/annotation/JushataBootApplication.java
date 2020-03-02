package com.didiglobal.jushata.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Configuration
@SpringBootApplication
public @interface JushataBootApplication {

    /**
     * 模块名
     * @return
     */
    String name() default "";

    /**
     * 父级模块
     * @return
     */
    String parent() default "";

    /**
     * http服务端口号
     * @return
     */
    int httpPort() default 0;

    /**
     * 启动顺序
     * @return
     */
    int order() default 0;

}
