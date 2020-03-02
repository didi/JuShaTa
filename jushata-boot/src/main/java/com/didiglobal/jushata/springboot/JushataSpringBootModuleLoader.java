package com.didiglobal.jushata.springboot;

import com.didiglobal.jushata.module.JushataModule;
import com.didiglobal.jushata.module.JushataModuleLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JushataSpringBootModuleLoader implements JushataModuleLoader {

    private static final Log                 LOGGER        = LogFactory.getLog(JushataSpringBootModuleLoader.class);

    private final Map<String, JushataModule> LOADED        = new HashMap<>();
    private final Map<String, Long>          MODIFIED_TIME = new HashMap<>();

    @Override
    public void load(ApplicationContext context) throws Exception {

        String val = context.getEnvironment().getProperty("jushata.modules-file");
        if (val == null || val.trim().length() == 0) {
            return;
        }

        File modules = new File(val);
        if (modules == null || !modules.exists() || !modules.isDirectory()) {
            return;
        }

        File[] jars = modules.listFiles(f -> f.isFile() && f.getName().endsWith(".jar"));
        Iterator<Map.Entry<String, JushataModule>> it = LOADED.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry<String, JushataModule> entry = it.next();
            if (!new File(entry.getKey()).exists()) {
                stop(entry, it);
            }
        }

        for (File jar : jars) {
            if (jar.exists() && !LOADED.containsKey(jar.getCanonicalPath())) {
                start(jar, context);
            }
        }

        it = LOADED.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, JushataModule> entry = it.next();
            File jar = new File(entry.getKey());
            if (jar.exists() && jar.lastModified() > MODIFIED_TIME.get(entry.getKey())) {
                LOGGER.info("[Jushata] Reload spring-boot-jushata-module " + jar);
                stop(entry, it);
                start(jar, context);
            }
        }
    }

    @Override
    public void load(ApplicationContext context, Object module) throws Exception {

        if (module instanceof String) {
            File jar = new File((String) module);
            if (jar.exists() && jar.isFile()) {
                JushataModule m = LOADED.get(jar.getCanonicalPath());
                if (m != null) {
                    LOGGER.info("[Jushata] Skip load already exists spring-boot-jushata-module " + module);
                    return;
                }

                start(jar, context);
            }
        }
    }

    @Override
    public void unload(Object module) {

        if (module instanceof String) {
            File jar = new File((String) module);
            if (jar.exists() && jar.isFile()) {
                try {
                    JushataModule m = LOADED.get(jar.getCanonicalPath());
                    if (m == null) {
                        LOGGER.info("[Jushata] Skip unload not exists spring-boot-jushata-module " + module);
                        return;
                    }

                    LOGGER.info("");
                    LOGGER.info("[Jushata] Unload spring-boot-jushata-module " + module);
                    m.stop();
                    LOADED.remove(jar.getCanonicalPath());
                    MODIFIED_TIME.remove(jar.getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void unload() {

        for (JushataModule jushataModule : LOADED.values()) {
            jushataModule.stop();
        }

        LOADED.clear();
        MODIFIED_TIME.clear();
    }

    private void stop(Map.Entry<String, JushataModule> entry, Iterator<Map.Entry<String, JushataModule>> it) {

        LOGGER.info("");
        LOGGER.info("[Jushata] Unload spring-boot-jushata-module " + entry.getValue());
        entry.getValue().stop();
        MODIFIED_TIME.remove(entry.getKey());
        it.remove();
    }

    private void start(File jar, ApplicationContext context) throws Exception {

        synchronized (jar.getCanonicalPath().intern()) {
            if (!LOADED.containsKey(jar.getCanonicalPath())) {

                LOGGER.info("");
                LOGGER.info("[Jushata] Load spring-boot-jushata-module " + jar);
                JushataModule jushataModule = new JushataSpringBootModule(jar, context);
                jushataModule.start();
                LOADED.put(jar.getCanonicalPath(), jushataModule);
                MODIFIED_TIME.put(jar.getCanonicalPath(), jar.lastModified());
            }
        }
    }

}
