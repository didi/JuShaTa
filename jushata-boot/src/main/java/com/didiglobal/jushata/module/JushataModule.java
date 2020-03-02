package com.didiglobal.jushata.module;

public interface JushataModule {

    String getName();

    void start() throws Exception;

    void stop();

}
