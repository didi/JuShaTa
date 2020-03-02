package com.didiglobal.sample.child1;

import com.didiglobal.jushata.annotation.JushataBootApplication;
import com.didiglobal.sample.api.SampleBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;

@ComponentScan
@ImportResource("classpath*:sample-child1.xml")
@EnableConfigurationProperties(SampleChild1Properties.class)
@JushataBootApplication(parent = "ROOT", httpPort = 8081)
public class SampleChild1AutoConfiguration {

    @Primary
    @Bean
    SampleBean javaSampleBean() {
        return SampleBean.builder().name("sample-child1").build();
    }

}
