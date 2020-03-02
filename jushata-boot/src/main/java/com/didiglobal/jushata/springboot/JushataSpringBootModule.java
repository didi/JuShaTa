package com.didiglobal.jushata.springboot;

import com.didiglobal.jushata.annotation.JushataBootApplication;
import com.didiglobal.jushata.classloader.JushataResourceLoader;
import com.didiglobal.jushata.module.JushataModule;
import com.didiglobal.jushata.module.JushataModuleManager;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JushataSpringBootModule implements JushataModule {

    private static final Log               LOGGER = LogFactory.getLog(JushataSpringBootModule.class);

    private File                           jar;
    private ApplicationContext             parent;
    private ConfigurableApplicationContext context;
    private ClassLoader                    classLoader;

    public JushataSpringBootModule(File jar, ApplicationContext parent) {
        this.jar = jar;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return this.jar.getName();
    }

    @Override
    public void start() throws Exception {

        this.createClassLoader();
        this.context = this.loadJushataModule();
        JushataModuleManager.getInstance().register(this);
    }

    @Override
    public void stop() {

        if (this.context != null) {
            this.context.close();
        }

        this.context = null;
        this.classLoader = null;
        JushataModuleManager.getInstance().unregister(this);
    }

    private void createClassLoader() throws IOException {

        List<URL> urls = new ArrayList<>();

        Archive archive = new JarFileArchive(this.jar);
        List<Archive> list = archive.getNestedArchives((entry) -> entry.isDirectory()
            ? entry.getName().equals("BOOT-INF/classes/") : entry.getName().startsWith("BOOT-INF/lib/"));

        urls.add(archive.getUrl());
        for (Archive obj : list) {
            urls.add(obj.getUrl());
        }

        this.classLoader = new LaunchedURLClassLoader(urls.toArray(new URL[0]),
            JushataApplication.class.getClassLoader());
    }

    private ConfigurableApplicationContext loadJushataModule() throws Exception {

        int httpPort = 0;
        List<Class<?>> sources = new ArrayList<>();

        Class<?> factoryClass = EnableAutoConfiguration.class;
        Set<String> difference = Sets.difference(
            new HashSet<>(SpringFactoriesLoader.loadFactoryNames(factoryClass, this.classLoader)),
            new HashSet<>(SpringFactoriesLoader.loadFactoryNames(factoryClass, this.classLoader.getParent())));
        for (String config : difference) {

            Class<?> configuration = ClassUtils.forName(config, this.classLoader);
            if (AnnotationUtils.findAnnotation(configuration, SpringBootApplication.class) != null) {
                sources.add(configuration);
                JushataBootApplication anno = AnnotationUtils.findAnnotation(configuration,
                    JushataBootApplication.class);
                if (anno != null) {
                    httpPort = anno.httpPort();
                }
            }
        }

        if (sources.isEmpty()) {
            return null;
        }

        return refreshContext(httpPort, sources);
    }

    private ConfigurableApplicationContext refreshContext(int httpPort, List<Class<?>> sources) {

        Thread.currentThread().setContextClassLoader(this.classLoader);
        CachedIntrospectionResults.acceptClassLoader(this.classLoader);

        try {

            if (this.parent instanceof AnnotationConfigServletWebServerApplicationContext
                || this.parent instanceof AnnotationConfigReactiveWebServerApplicationContext) {
                sources.add(JushataAutoConfiguration.DispatcherServletConfiguration.class);
                sources.add(JushataAutoConfiguration.EnableWebMvcConfiguration.class);
            }

            LOGGER.info("[Jushata] Load spring-boot-jushata-module " + sources);

            DefaultResourceLoader resourceLoader = new JushataResourceLoader(this.classLoader);
            SpringApplication app = new SpringApplication(resourceLoader, sources.toArray(new Class[0]));
            app.setBannerMode(Banner.Mode.OFF);
            app.setLogStartupInfo(false);
            app.addInitializers(new JushataApplicationContextInitializer(this.parent, httpPort));
            app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {

                final String port = event.getEnvironment().getProperty("server.port", String.valueOf(httpPort));
                event.getSpringApplication().setWebEnvironment(Integer.valueOf(port) > 0);
            });

            return app.run();

        } finally {
            CachedIntrospectionResults.clearClassLoader(this.classLoader);
            Thread.currentThread().setContextClassLoader(JushataApplication.class.getClassLoader());
        }
    }
}
