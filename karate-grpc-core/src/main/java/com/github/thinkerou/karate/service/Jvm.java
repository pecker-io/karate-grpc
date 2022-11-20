package com.github.thinkerou.karate.service;

import com.github.thinkerou.karate.constants.DescriptorFile;
import com.google.common.base.Preconditions;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Jvm {

  public static final String PROTOBIN_DIR = "protobin.dir";

  private Jvm() {
    //defeat instantiation
  }

  public static boolean isProtoBinDirSpecifiedViaJVMArguments() {
    return !System.getProperty(PROTOBIN_DIR, "").trim().isEmpty();
  }

  public static List<String> getProtoBinFiles() {
    String location = System.getProperty(PROTOBIN_DIR);
    String errorMsg = String.format("Please specific the directory that contains the "
        + "protobuf binary file(s) via the JVM argument -D%s=<locationGoesHere>", PROTOBIN_DIR);
    Preconditions.checkNotNull(location, errorMsg);
    Preconditions.checkArgument(!location.isEmpty(), errorMsg);
    File directory = Paths.get(location).toFile();
    Preconditions.checkArgument(directory.isDirectory(), errorMsg);
    File[] files = Optional.ofNullable(
            directory.listFiles(file -> file.getName().toLowerCase().endsWith(".protobin")))
        .orElse(new File[0]);
    errorMsg = "No protobin files (*.protobin) found in directory " + location;
    Preconditions.checkArgument(files.length != 0, errorMsg);
    List<String> result = Arrays.stream(files)
        .filter(each -> !each.isDirectory())
        .map(File::getAbsolutePath)
        .collect(Collectors.toCollection(ArrayList::new));
    defaultProtobufFile().ifPresent(result::add);
    return result;
  }

  private static Optional<String> defaultProtobufFile() {
    String path = System.getProperty("user.home") + DescriptorFile.PROTO_PATH.getText();
    File file = Paths.get(path + DescriptorFile.PROTO_FILE.getText()).toFile();
    if (file.isFile() && file.exists()) {
      return Optional.of(file.getAbsolutePath());
    }
    return Optional.empty();
  }


}
