package com.didiglobal.sample.child1;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.application")
public class SampleChild1Properties {

    String id;
    String name;

}
