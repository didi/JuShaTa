package com.didiglobal.sample.child2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.application")
public class SampleChild2Properties {

    String id;
    String name;
}
