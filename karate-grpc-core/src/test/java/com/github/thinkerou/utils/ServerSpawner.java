package com.github.thinkerou.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ServerSpawner {

  private Process process;
  private final File logFile = new File(System.getProperty("user.dir") + "/target/server.log");
  private static final Logger log = Logger.getLogger(ServerSpawner.class.getName());

  //To add new tests that involve invoking the server.jar the following needs to be done
  //1. Add the changes to the test project artifacts/sample-project-for-unit-tests/test-app-protos
  //2. Build the project to generate the "server.jar"
  //3. Copy the jar "artifacts/sample-project-for-unit-tests/test-app-protos/target/server.jar" to
  // "artifacts/jars"
  //4. Copy the protobin file "artifacts/sample-project-for-unit-tests/test-app-protos/target/classes/test-app-protos.protobin" to
  // "artifacts/protobins"

  public void start() throws IOException {
    List<String> commands = Arrays.asList(
        "java",
        "-jar",
        "artifacts/jars/server.jar"
    );
    process = new ProcessBuilder(commands)
        .redirectErrorStream(true)
        .redirectError(logFile)
        .start();
  }

  public void stop() throws IOException, InterruptedException {
    process = process.destroyForcibly();
    process.waitFor();
    if (logFile.exists()) {
      List<String> lines = Files.readAllLines(Paths.get(logFile.getAbsolutePath()));
      if (!lines.isEmpty()) {
        log.info("Errors : " + lines);
      }
    }
  }
}
