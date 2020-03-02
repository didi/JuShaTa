package com.didiglobal.jushata.module;

import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class JushataModuleManager {

    private static final JushataModuleManager INSTANCE = new JushataModuleManager();
    private final List<JushataModule>         modules  = new ArrayList<>();

    private ConfigurableApplicationContext    context;
    private List<JushataModuleLoader>         loaders;

    public static JushataModuleManager getInstance() {
        return INSTANCE;
    }

    public void register(JushataModule module) {
        this.modules.add(module);
    }

    public void unregister(JushataModule module) {
        this.modules.remove(module);
    }

    public void init(ConfigurableApplicationContext context, List<JushataModuleLoader> loaders) {
        this.context = context;
        this.loaders = loaders;
    }

    public List<JushataModule> getModules() {
        return this.modules;
    }

    public void load(Object module) throws Exception {

        for (JushataModuleLoader loader : loaders) {
            loader.load(this.context, module);
        }
    }

    public void unload(Object moduleName) {

        for (JushataModuleLoader loader : loaders) {
            loader.unload(moduleName);
        }
    }
}
