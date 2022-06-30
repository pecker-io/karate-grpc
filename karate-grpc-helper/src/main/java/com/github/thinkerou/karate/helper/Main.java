package com.github.thinkerou.karate.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.thinkerou.karate.constants.DescriptorFile;
import com.github.thinkerou.karate.utils.RedisHelper;

/**
 * Main
 *
 * @author thinkerou
 */
public class Main {

    public static void main(String[] args) {
        putTestDescriptorSetsToRedis();
    }

    public static void putTestDescriptorSetsToRedis() {
        putDescriptorSetsToRedis(DescriptorFile.PROTO_PATH.getText(), DescriptorFile.PROTO_FILE.getText());
    }

    public static void putDescriptorSetsToRedis(String protoPath, String protoFile) {
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

        RedisHelper redisHelper = new RedisHelper();
        if (redisHelper.getDescriptorSets() != null) {
            redisHelper.deleteDescriptorSets();
        }
        Boolean b = redisHelper.putDescriptorSets(descriptorPath);
        System.out.println("Put descriptor sets success: " + b.toString());
    }

}
