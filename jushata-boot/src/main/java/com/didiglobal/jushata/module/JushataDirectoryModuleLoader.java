package com.didiglobal.jushata.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JushataDirectoryModuleLoader implements JushataModuleLoader {

    private static final Log                 LOGGER        = LogFactory.getLog(JushataDirectoryModuleLoader.class);

    private final Map<String, JushataModule> LOADED        = new HashMap<>();
    private final Map<String, Long>          MODIFIED_TIME = new HashMap<>();
    private final static String              CONFIG        = "conf/application.properties";

    @Override
    public void load(ApplicationContext context) throws Exception {

        String val = context.getEnvironment().getProperty("jushata.modules-dir");
        if (val == null || val.trim().length() == 0) {
            return;
        }

        File modules = new File(val);
        if (modules == null || !modules.exists() || !modules.isDirectory()) {
            return;
        }

        File[] directories = modules.listFiles(f -> f.isDirectory() && new File(f, CONFIG).exists());
        Iterator<Map.Entry<String, JushataModule>> it = LOADED.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, JushataModule> entry = it.next();
            if (!new File(entry.getKey()).exists()) {
                stop(entry, it);
            }
        }

        for (File dir : directories) {
            if (dir.exists() && !LOADED.containsKey(dir.getCanonicalPath())) {
                start(dir, context);
            }
        }

        it = LOADED.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, JushataModule> entry = it.next();
            File dir = new File(entry.getKey());
            if (dir.exists() && new File(dir, CONFIG).lastModified() > MODIFIED_TIME.get(entry.getKey())) {
                LOGGER.info("[Jushata] Reload directory-jushata-module " + dir);
                stop(entry, it);
                start(dir, context);
            }
        }
    }

    @Override
    public void load(ApplicationContext context, Object module) throws Exception {

        if (module instanceof String) {
            File dir = new File((String) module);
            if (dir.exists() && dir.isDirectory() && new File(dir, CONFIG).exists()) {
                JushataModule m = LOADED.get(dir.getCanonicalPath());
                if (m != null) {
                    LOGGER.info("[Jushata] Skip load already exists directory-jushata-module " + module);
                    return;
                }

                start(dir, context);
            }
        }
    }

    @Override
    public void unload(Object module) {

        if (module instanceof String) {
            File dir = new File((String) module);
            if (dir.exists() && dir.isDirectory() && new File(dir, CONFIG).exists()) {
                try {
                    JushataModule m = LOADED.get(dir.getCanonicalPath());
                    if (m == null) {
                        LOGGER.info("[Jushata] Skip unload not exists directory-jushata-module " + module);
                        return;
                    }

                    LOGGER.info("");
                    LOGGER.info("[Jushata] Unload directory-jushata-module " + module);
                    m.stop();
                    LOADED.remove(dir.getCanonicalPath());
                    MODIFIED_TIME.remove(dir.getCanonicalPath());
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
        LOGGER.info("[Jushata] Unload directory-jushata-module " + entry.getValue());
        entry.getValue().stop();
        MODIFIED_TIME.remove(entry.getKey());
        it.remove();
    }

    private void start(File dir, ApplicationContext context) throws Exception {

        synchronized (dir.getCanonicalPath().intern()) {
            if (!LOADED.containsKey(dir.getCanonicalPath())) {

                LOGGER.info("");
                LOGGER.info("[Jushata] Load directory-jushata-module " + dir);
                JushataModule jushataModule = new JushataDirectoryModule(dir);
                jushataModule.start();
                LOADED.put(dir.getCanonicalPath(), jushataModule);
                MODIFIED_TIME.put(dir.getCanonicalPath(), new File(dir, "conf/application.properties").lastModified());
            }
        }
    }

}
