package com.didiglobal.jushata.classloader;

import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;

public interface Resource {

    byte[] getContent();

    Certificate[] getCertificates();

    Manifest getManifest();

    URL getCodeBase();
}
