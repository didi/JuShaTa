package com.didiglobal.sample.standard;

import com.didiglobal.sample.api.SampleBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@ComponentScan
@ImportResource("classpath*:sample-standard.xml")
public class SampleStandardAutoConfiguration {

    @Bean
    SampleBean javaSampleBean() {
        return SampleBean.builder().name("sample-standard").build();
    }

}
