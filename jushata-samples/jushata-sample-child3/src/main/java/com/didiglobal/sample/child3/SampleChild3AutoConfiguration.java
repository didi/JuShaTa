package com.didiglobal.sample.child3;

import com.didiglobal.sample.api.SampleBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;

@ComponentScan
@ImportResource("classpath*:sample-child3.xml")
@EnableConfigurationProperties(SampleChild3Properties.class)
@SpringBootApplication
public class SampleChild3AutoConfiguration {

    static ConfigurableApplicationContext ctx;

    @Primary
    @Bean
    SampleBean javaSampleBean() {
        return SampleBean.builder().name("sample-child3").build();
    }

    public static void main(String[] args) {
        start(args);
    }

    public static ConfigurableApplicationContext start(String[] args) {
        ctx = SpringApplication.run(SampleChild3AutoConfiguration.class, args);
        return ctx;
    }

    public static void stop(String[] args) {
        ctx.close();
    }
}
