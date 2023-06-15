package com.github.thinkerou.karate.utils;

import com.github.thinkerou.karate.protobuf.ServiceResolver;
import com.github.thinkerou.karate.service.Jvm;
import com.google.common.io.ByteStreams;
import com.google.protobuf.DescriptorProtos;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class DataReader {

  private static final Logger log = Logger.getLogger(DataReader.class.getName());

  private static volatile List<ServiceResolver> resolverCache;

  private DataReader() {
  }

  public static List<ServiceResolver> read(RedisHelper redisHelper) {
    if (resolverCache != null) {
      return resolverCache;
    }
    resolverCache = _read(redisHelper);
    return resolverCache;
  }

  private static synchronized List<ServiceResolver> _read(RedisHelper redisHelper) {
    List<byte[]> data;
    if (redisHelper != null) {
      data = Collections.singletonList(redisHelper.getDescriptorSets());
    } else {
      data = mapper().values()
          .stream()
          .map(each -> {
            try {
              return ByteStreams.toByteArray(each);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          })
          .collect(Collectors.toList());
    }

    List<ServiceResolver> resolvers = new ArrayList<>();
    for (byte[] datum: data) {
      // Fetch the appropriate file descriptors for the service.
      DescriptorProtos.FileDescriptorSet fileDescriptorSet;
      try {
        fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(datum);
      } catch (IOException e) {
        throw new IllegalArgumentException("Descriptor file parse fail: " + e.getMessage());
      }
      resolvers.add(ServiceResolver.fromFileDescriptorSet(fileDescriptorSet));
    }
    return resolvers;
  }

  private static Map<String, InputStream> mapper() {
    Map<String, InputStream> mapping = new HashMap<>();
    if (Jvm.isProtoBinDirSpecifiedViaJVMArguments()) {
      Function<String, InputStream> asStream = fileName -> {
        try {
          return new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
        }
      };
      Jvm.getProtoBinFiles()
          .forEach(file -> mapping.put(file, asStream.apply(file)));
    }
    else {
      log.info("Will search through the CLASSPATH for *.protobin files.");
      try (ScanResult scanResult = new ClassGraph().scan()) {
        scanResult.getResourcesWithExtension("protobin")
            .forEachByteArrayIgnoringIOException(
                (Resource res, byte[] content) ->
                    mapping.put(res.getPath(), new ByteArrayInputStream(content))
            );
      }
    }
    return mapping;
  }

}
