package com.didiglobal.jushata.springboot;

import com.didiglobal.jushata.module.JushataModuleLoader;
import com.didiglobal.jushata.module.JushataModuleManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class JushataApplication {

    private static final Log                 LOGGER  = LogFactory.getLog(JushataApplication.class);

    final static AtomicBoolean               LOADING = new AtomicBoolean(false);
    final static int                         PERIOD  = 60;
    final static ScheduledThreadPoolExecutor executor;

    static {
        executor = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("jushata-loader").setDaemon(true).build());
    }

    public static ConfigurableApplicationContext run(Class<?> source, String... args) {

        return new SpringApplicationBuilder(source).web(WebApplicationType.SERVLET).build().run(args);
    }

    public void run(ConfigurableApplicationContext context) {

        List<JushataModuleLoader> loaders = SpringFactoriesLoader.loadFactories(JushataModuleLoader.class,
            JushataApplication.class.getClassLoader());

        load(loaders, context, true);

        if (!executor.isTerminated() && !executor.isTerminating() && !executor.isShutdown()) {
            executor.scheduleAtFixedRate(() -> load(loaders, context, false), PERIOD, PERIOD, TimeUnit.SECONDS);
        }
    }

    private void load(List<JushataModuleLoader> loaders, ConfigurableApplicationContext context, boolean init) {

        if (!LOADING.get()) {
            try {
                LOADING.set(true);
                JushataModuleManager.getInstance().init(context, loaders);
                for (JushataModuleLoader loader : loaders) {
                    loader.load(context);
                }
            } catch (Throwable e) {
                if (init) {
                    LOGGER.error("[Jushata] Stop JushataApplication", e);
                    for (JushataModuleLoader loader : loaders) {
                        loader.unload();
                    }

                    executor.shutdownNow();
                    context.close();
                }
                LOGGER.error("", e);
            } finally {
                LOADING.set(false);
            }
        }
    }

}
