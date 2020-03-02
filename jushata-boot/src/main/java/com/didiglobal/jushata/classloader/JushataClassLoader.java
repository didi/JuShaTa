package com.didiglobal.jushata.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.security.CodeSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JushataClassLoader extends URLClassLoader {

    protected boolean             delegate = false;
    private ClassLoader           javaseClassLoader;
    private Map<String, Class<?>> cache    = new ConcurrentHashMap<>();

    public JushataClassLoader(URL[] urls, ClassLoader parent) {

        super(urls, parent);

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.javaseClassLoader = j;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        Class<?> clazz;

        clazz = findLoadedClass(name);
        if (clazz != null) {
            return resolveClass(clazz, resolve);
        }

        String resourceName = binaryNameToPath(name, false);

        // 1.
        boolean tryLoadingFromJavaseLoader;
        try {
            tryLoadingFromJavaseLoader = javaseClassLoader.getResource(resourceName) != null;
        } catch (Throwable t) {
            handleThrowable(t);
            tryLoadingFromJavaseLoader = true;
        }

        if (tryLoadingFromJavaseLoader) {
            try {
                clazz = javaseClassLoader.loadClass(name);
                if (clazz != null) {
                    return resolveClass(clazz, resolve);
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        boolean delegateLoad = delegate || filter(name, true);

        // 2. 公共类库使用父类classloader加载
        if (delegateLoad) {
            try {
                clazz = Class.forName(name, false, this.getParent());
                if (clazz != null) {
                    return resolveClass(clazz, resolve);
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        // 3. 优先自己加载，破坏双亲委派
        try {
            clazz = findClass(name);
            if (clazz != null) {
                return resolveClass(clazz, resolve);
            }
        } catch (ClassNotFoundException e) {
            // Ignore
        }

        // 4. 自己加载不到时，使用父类classloader加载
        if (!delegateLoad) {
            try {
                clazz = Class.forName(name, false, getParent());
                if (clazz != null) {
                    return resolveClass(clazz, resolve);
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        Class<?> clazz;

        try {
            try {
                clazz = findClassInternal(name);
            } catch (AccessControlException ace) {
                throw new ClassNotFoundException(name, ace);
            } catch (RuntimeException e) {
                throw e;
            }

            if (clazz == null) {
                try {
                    clazz = super.findClass(name);
                } catch (AccessControlException ace) {
                    throw new ClassNotFoundException(name, ace);
                } catch (RuntimeException e) {
                    throw e;
                }
            }

            if (clazz == null) {
                throw new ClassNotFoundException(name);
            }
        } catch (ClassNotFoundException e) {
            throw e;
        }

        return clazz;
    }

    /*
    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        return super.findResources(name);
    }

    @Override
    public URL findResource(String name) {
        return super.findResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return super.getResourceAsStream(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return super.getResources(name);
    }

    @Override
    public URL getResource(String name) {
        return super.getResource(name);
    }
    */

    private Class<?> resolveClass(Class<?> clazz, boolean resolve) {
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }

    protected boolean filter(String name, boolean isClassName) {

        return true;
    }

    protected Class<?> findClassInternal(String className) {

        if (cache.get(className) != null) {
            return cache.get(className);
        }

        String classEntryName = binaryNameToPath(className, false);
        Resource resource = new SpringBootFatJarResource(getURLs(), classEntryName);
        if (resource == null) {
            return null;
        }

        Class<?> clazz;
        synchronized (getClassLoadingLock(className)) {

            String packageName = null;
            int pos = className.lastIndexOf('.');
            if (pos != -1) {
                packageName = className.substring(0, pos);
            }

            if (packageName != null) {
                Package pkg = getPackage(packageName);
                if (pkg == null) {
                    try {
                        definePackage(packageName, resource.getManifest(), resource.getCodeBase());
                    } catch (IllegalArgumentException e) {
                        // Ignore: normal error due to dual definition of package
                    }
                    getPackage(packageName);
                }
            }

            try {
                byte[] binaryContent = resource.getContent();
                clazz = defineClass(className, binaryContent, 0, binaryContent.length,
                    new CodeSource(resource.getCodeBase(), resource.getCertificates()));
            } catch (UnsupportedClassVersionError ucve) {
                throw new UnsupportedClassVersionError(
                    ucve.getLocalizedMessage() + " (unable to load class [" + className + "])");
            }

            cache.put(className, clazz);
        }

        return clazz;
    }

    private String binaryNameToPath(String binaryName, boolean withLeadingSlash) {

        // 1 for leading '/', 6 for ".class"
        StringBuilder path = new StringBuilder(7 + binaryName.length());
        if (withLeadingSlash) {
            path.append('/');
        }

        path.append(binaryName.replace('.', '/'));
        path.append(".class");

        return path.toString();
    }

    private static void handleThrowable(Throwable t) {

        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }

        if (t instanceof StackOverflowError) {
            // Swallow silently - it should be recoverable
            return;
        }

        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
        // All other instances of Throwable will be silently swallowed
    }

}
