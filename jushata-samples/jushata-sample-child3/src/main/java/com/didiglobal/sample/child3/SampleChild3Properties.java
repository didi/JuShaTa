package com.didiglobal.sample.child3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.application")
public class SampleChild3Properties {

    String id;
    String name;

}
