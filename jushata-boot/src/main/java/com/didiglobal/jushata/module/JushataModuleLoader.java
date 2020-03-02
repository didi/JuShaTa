package com.didiglobal.jushata.module;

import org.springframework.context.ApplicationContext;

public interface JushataModuleLoader {

    void load(ApplicationContext context) throws Exception;

    void load(ApplicationContext context, Object module) throws Exception;

    void unload(Object module);

    void unload();

}
