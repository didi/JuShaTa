package com.didiglobal.jushata.springboot;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class JushataApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        if (event.getApplicationContext().getParent() == null) {
            new JushataApplication().run(event.getApplicationContext());
        }
    }

}
