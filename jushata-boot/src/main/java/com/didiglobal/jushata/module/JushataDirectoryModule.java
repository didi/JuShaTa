package com.didiglobal.jushata.module;

import com.didiglobal.jushata.springboot.JushataApplication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.CachedIntrospectionResults;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JushataDirectoryModule implements JushataModule {

    private static final Log LOGGER = LogFactory.getLog(JushataDirectoryModule.class);

    private File             dir;
    private ClassLoader      classLoader;
    private String           mainClassName;

    public JushataDirectoryModule(File dir) {
        this.dir = dir;
    }

    @Override
    public String getName() {
        return this.dir.getName();
    }

    @Override
    public void start() throws Exception {

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(this.dir, "conf/application.properties")));
        this.mainClassName = properties.getProperty("main-class", "");

        this.createClassLoader();
        this.loadJushataModule();

        JushataModuleManager.getInstance().register(this);
    }

    @Override
    public void stop() {

        Thread.currentThread().setContextClassLoader(this.classLoader);
        CachedIntrospectionResults.acceptClassLoader(this.classLoader);

        try {

            LOGGER.info("[Jushata] Unload directory-jushata-module " + mainClassName);

            Class<?> mainClass = this.classLoader.loadClass(mainClassName);
            Method mainMethod = mainClass.getDeclaredMethod("stop", String[].class);
            mainMethod.invoke(null, new Object[] { new String[0] });

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            CachedIntrospectionResults.clearClassLoader(this.classLoader);
            Thread.currentThread().setContextClassLoader(JushataApplication.class.getClassLoader());
            this.classLoader = null;
            JushataModuleManager.getInstance().unregister(this);
        }
    }

    private void createClassLoader() throws IOException {

        List<URL> urls = new ArrayList<>();

        urls.add(new File(dir, "conf").toURL());
        File[] jars = new File(dir, "lib").listFiles((file) -> file.isFile() && file.getName().endsWith(".jar"));
        for (File j : jars) {
            urls.add(j.toURL());
        }

        this.classLoader = new URLClassLoader(urls.toArray(new URL[0]),
            JushataApplication.class.getClassLoader().getParent());
    }

    private void loadJushataModule() throws Exception {

        Thread.currentThread().setContextClassLoader(this.classLoader);
        CachedIntrospectionResults.acceptClassLoader(this.classLoader);

        LOGGER.info("[Jushata] Load directory-jushata-module " + mainClassName);

        try {
            String cls = "org.apache.catalina.webresources.TomcatURLStreamHandlerFactory";
            Class<?> tomcatURLStreamHandlerFactory = this.classLoader.loadClass(cls);
            tomcatURLStreamHandlerFactory.getDeclaredMethod("disable").invoke(null);
        } catch (Throwable e) {
        }

        try {

            Class<?> mainClass = this.classLoader.loadClass(mainClassName);
            Method mainMethod = mainClass.getDeclaredMethod("start", String[].class);
            mainMethod.invoke(null, new Object[] { new String[0] });

        } finally {
            CachedIntrospectionResults.clearClassLoader(this.classLoader);
            Thread.currentThread().setContextClassLoader(JushataApplication.class.getClassLoader());
        }
    }

}
