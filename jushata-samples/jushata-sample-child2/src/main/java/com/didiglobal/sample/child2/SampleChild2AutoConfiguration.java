package com.didiglobal.sample.child2;

import com.didiglobal.jushata.annotation.JushataBootApplication;
import com.didiglobal.sample.api.SampleBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;

@ComponentScan
@ImportResource("classpath*:sample-child2.xml")
@EnableConfigurationProperties(SampleChild2Properties.class)
@JushataBootApplication(parent = "ROOT", httpPort = 8082)
public class SampleChild2AutoConfiguration {

    @Primary
    @Bean
    SampleBean javaSampleBean() {
        return SampleBean.builder().name("sample-child2").build();
    }

}
