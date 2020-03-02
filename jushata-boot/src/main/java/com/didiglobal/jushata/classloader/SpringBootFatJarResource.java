package com.didiglobal.jushata.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class SpringBootFatJarResource implements Resource {

    private JarEntry      resource;
    private InputStream   inputStream;
    private Manifest      manifest;
    private URL           codeBase;
    private Certificate[] certificates;

    @Override
    public Certificate[] getCertificates() {
        return certificates;
    }

    @Override
    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public URL getCodeBase() {
        return codeBase;
    }

    public SpringBootFatJarResource(URL[] urls, String path) {

        for (URL url : urls) {
            try {
                URLConnection connection = url.openConnection();
                if (connection instanceof JarURLConnection) {
                    JarFile jarFile = ((JarURLConnection) connection).getJarFile();
                    if (jarFile.getEntry(path) != null && jarFile.getManifest() != null) {

                        this.resource = jarFile.getJarEntry(path);
                        this.inputStream = jarFile.getInputStream(this.resource);
                        this.certificates = this.resource.getCertificates();
                        this.manifest = jarFile.getManifest();
                        this.codeBase = url;
                    }
                }
            } catch (IOException ex) {
                // Ignore
            }
        }
    }

    @Override
    public byte[] getContent() {

        long len = this.resource.getSize();

        if (len > Integer.MAX_VALUE) {
            // Can't create an array that big
            throw new ArrayIndexOutOfBoundsException(String.format(
                "Unable to return [{0}] as a byte array since the resource is [%d] bytes in size "
                        + "which is larger than the maximum size of a byte array", len));
        }

        if (len < 0) {
            // Content is not applicable here (e.g. is a directory)
            return null;
        }

        int size = (int) len;
        byte[] result = new byte[size];

        int pos = 0;
        try {
            if (this.inputStream == null) {
                // An error occurred, don't return corrupted content
                return null;
            }
            while (pos < size) {
                int n = this.inputStream.read(result, pos, size - pos);
                if (n < 0) {
                    break;
                }
                pos += n;
            }
            // Once the stream has been read, read the certs
            // this.certificates = this.resource.getCertificates();
        } catch (IOException ioe) {
            // Don't return corrupted content
            return null;
        }

        return result;
    }
}
