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
        String path = System.getProperty("user.home") + DescriptorFile.PROTO_PATH.getText();
        new File(path).mkdirs();
        Path descriptorPath = Paths.get(path + DescriptorFile.PROTO_FILE.getText());
        if (!Files.exists(descriptorPath)) {
            try {
                new File(descriptorPath.toString()).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
