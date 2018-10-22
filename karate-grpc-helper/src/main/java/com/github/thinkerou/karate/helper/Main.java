package com.github.thinkerou.karate.helper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.thinkerou.karate.utils.RedisHelper;

/**
 * Main
 *
 * @author thinkerou
 */
public class Main {

    private static String DESCRIPTOR_FILE = "/helper/protobuf/descriptor-sets/karate-grpc.protobin";

    public static void main(String[] args) {
        Path descriptorPath = Paths.get(System.getProperty("user.dir") + DESCRIPTOR_FILE);
        if (!Files.exists(descriptorPath)) {
            throw new IllegalArgumentException("Path not exists: " + descriptorPath.toString());
        }

        String host = "localhost";
        RedisHelper redisHelper = RedisHelper.create(host, 6379);
        if (redisHelper.getDescriptorSets() != null) {
            redisHelper.deleteDescriptorSets();
        }
        Boolean b = redisHelper.putDescriptorSets(descriptorPath);
        System.out.println("Put descriptor sets success: " + b.toString());
    }

}
