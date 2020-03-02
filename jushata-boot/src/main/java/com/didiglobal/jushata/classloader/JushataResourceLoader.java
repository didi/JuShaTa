package com.didiglobal.jushata.classloader;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JushataResourceLoader extends DefaultResourceLoader {

    private final Set<String> fileExtensions = new HashSet<>();
    private final Set<String> classPaths     = new HashSet<>(
        Arrays.asList(new String[] { "classpath:", "classpath:/config/" }));

    public JushataResourceLoader(@Nullable ClassLoader classLoader) {
        super(classLoader);
        List<PropertySourceLoader> propertySourceLoaders = SpringFactoriesLoader
            .loadFactories(PropertySourceLoader.class, getClass().getClassLoader());
        for (PropertySourceLoader loader : propertySourceLoaders) {
            fileExtensions.addAll(Arrays.asList(loader.getFileExtensions()));
        }
    }

    @Override
    public Resource getResource(String location) {

        if (!isConfigFile(location) || !(this.getClassLoader() instanceof URLClassLoader)) {
            return super.getResource(location);
        }

        Resource resource = new JarClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()),
            this.getClassLoader());
        if (!resource.exists()) {
            resource = super.getResource(location);
        }

        return resource;
    }

    private boolean isConfigFile(String location) {

        for (String classpath : classPaths) {
            if (location.startsWith(classpath)) {
                for (String fileExtension : fileExtensions) {
                    if (location.endsWith(fileExtension)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    class JarClassPathResource extends ClassPathResource {

        public JarClassPathResource(String path, @Nullable ClassLoader classLoader) {
            super(path, classLoader);
        }

        @Override
        protected URL resolveURL() {
            URL url = ((URLClassLoader) this.getClassLoader()).findResource(this.getPath());
            if (url == null) {
                url = super.resolveURL();
            }
            return url;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            URL url = this.resolveURL();
            return url != null ? url.openStream() : null;
        }
    }

}
