package com.github.thinkerou.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

public class JarClassLoader extends URLClassLoader {

  private static final Logger log = Logger.getLogger(JarClassLoader.class.getName());
  private final URL url;

  private JarClassLoader(URL url, ClassLoader parent) {
    super(new URL[]{url}, parent);
    this.url = url;
  }

  private void load() {
    addURL(url);
  }

  public static void loadLibrary() {
    File jar = new File(System.getProperty("user.dir") + "/artifacts/jars/server.jar");
    log.warning("Adding the jar " + jar.getAbsolutePath() + " to the classpath");
    try {
      URL url = jar.toURI().toURL();

      ClassLoader loader = ClassLoader.getSystemClassLoader();
      JarClassLoader jarClassLoader = new JarClassLoader(url, loader);
      Thread.currentThread().setContextClassLoader(jarClassLoader);
      jarClassLoader.load();
    } catch (Exception ex) {
      throw new RuntimeException(
          "Cannot load library from jar file '" + jar.getAbsolutePath() + "'. Reason: "
              + ex.getMessage(), ex);
    }
  }
}
