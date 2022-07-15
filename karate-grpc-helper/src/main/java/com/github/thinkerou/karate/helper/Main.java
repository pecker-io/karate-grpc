package com.github.thinkerou.karate.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.thinkerou.karate.constants.DescriptorFile;
import com.github.thinkerou.karate.utils.MockRedisHelperSingleton;
import com.github.thinkerou.karate.utils.RedisHelper;

/**
 * Main
 *
 * @author thinkerou
 */
public class Main {

    /**
     * @param args args
     */
    public static void main(String[] args) {
        putTestDescriptorSetsToRedis();
    }

    public static void putTestDescriptorSetsToRedis() {
        putDescriptorSetsToRedis(MockRedisHelperSingleton.INSTANCE.getRedisHelper(), DescriptorFile.PROTO_PATH.getText(), DescriptorFile.PROTO_FILE.getText());
    }

    /**
     * @param redisHelper redis helper
     * @param protoPath proto path
     * @param protoFile proto file
     */
    public static void putDescriptorSetsToRedis(RedisHelper redisHelper, String protoPath, String protoFile) {
        String path = System.getProperty("user.home") + protoPath;
        new File(path).mkdirs();
        Path descriptorPath = Paths.get(path + protoFile);

        if (!Files.exists(descriptorPath)) {
            try {
                new File(descriptorPath.toString()).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (redisHelper.getDescriptorSets() != null) {
            redisHelper.deleteDescriptorSets();
        }
        Boolean b = redisHelper.putDescriptorSets(descriptorPath);
        System.out.println("Put descriptor sets success: " + b.toString());
    }

}
