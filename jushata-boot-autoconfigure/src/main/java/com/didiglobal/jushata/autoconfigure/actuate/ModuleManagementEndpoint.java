package com.didiglobal.jushata.autoconfigure.actuate;

import com.didiglobal.jushata.module.JushataModule;
import com.didiglobal.jushata.module.JushataModuleManager;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;

@Endpoint(id = "modules")
public class ModuleManagementEndpoint {

    @ReadOperation
    List<JushataModule> contexts() {

        return JushataModuleManager.getInstance().getModules();
    }

    @ReadOperation
    public JushataModule context(@Selector String arg0) {

        Assert.notNull(arg0, "Name must not be null");
        List<JushataModule> modules = JushataModuleManager.getInstance().getModules();
        for (JushataModule module : modules) {
            if (module.getName().equals(arg0)) {
                return module;
            }
        }

        return null;
    }

    @WriteOperation
    public String action(@Selector String arg0, @Nullable Action action, @Nullable String path) {

        Assert.notNull(arg0, "Name must not be empty");

        try {
            if (action == Action.install) {

                JushataModuleManager.getInstance().load(path);

            } else if (action == Action.uninstall) {

                JushataModuleManager.getInstance().unload(path);
            }
        } catch (Exception e) {

            return "{}";
        }

        return "{}";
    }

    public enum Action {

                        install, uninstall
    }

}
