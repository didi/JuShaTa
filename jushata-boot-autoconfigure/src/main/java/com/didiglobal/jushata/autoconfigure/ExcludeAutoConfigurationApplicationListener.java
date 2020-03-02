package com.didiglobal.jushata.autoconfigure;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Arrays;

public class ExcludeAutoConfigurationApplicationListener implements ApplicationListener<ApplicationPreparedEvent> {

    final static String key = "spring.autoconfigure.exclude";

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {

        if (event.getApplicationContext().getParent() != null) {

            ConfigurableEnvironment env = event.getApplicationContext().getEnvironment();
            HashMultiset<String> values = HashMultiset.create();

            String excludes = env.getProperty(key);
            if (excludes != null && excludes.trim().length() > 0) {
                values.addAll(Arrays.asList(excludes.split(",")));
            }

            excludes = env.getProperty("jushata." + key);
            if (excludes != null && excludes.trim().length() > 0) {
                values.addAll(Arrays.asList(excludes.split(",")));
                env.getPropertySources().addFirst(new MapPropertySource("jushata",
                    ImmutableMap.<String, Object> builder().put(key, Joiner.on(",").join(values)).build()));
            }
        }
    }

}
