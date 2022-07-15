package com.github.thinkerou.demo.integration;

import java.io.IOException;
import java.util.logging.Logger;

import com.github.thinkerou.karate.GrpcClient;
import com.github.thinkerou.karate.RedisGrpcClient;
import com.github.thinkerou.karate.utils.FileHelper;
import com.github.thinkerou.karate.utils.MockRedisHelperSingleton;

/**
 * GrpcClientDemo
 *
 * @author thinkerou
 */
public class GrpcClientDemo {

    private static final Logger logger = Logger.getLogger(GrpcClientDemo.class.getName());

    /**
     * @param args args
     * @throws IOException io exception
     */
    public static void main(String[] args) throws IOException {
        GrpcClient client1 = new GrpcClient();
        String result1 = client1.list("Greeter", "SayHello", true);
        logger.info(result1);
        result1 = client1.list("helloworld.Greeter/SayHello", true);
        logger.info(result1);

        String file = System.getProperty("user.dir") + "/src/test/java/demo/helloworld/helloworld.json";
        logger.info(file);
        String payloads = FileHelper.readFile(file);
        logger.info(payloads);

        // Note: for testing
        // Need to start helloworld server first
        // using the following command:
        //   cd karate-demo
        //   mvn exec:java -Dexec.mainClass=com.github.thinkerou.demo.helloworld.HelloWorldServerMain
        GrpcClient client2 = new RedisGrpcClient("localhost", 50051, MockRedisHelperSingleton.INSTANCE.getRedisHelper());
        String result2 = client2.call("helloworld.Greeter/SayHello", payloads);
        logger.info(result2);

        // Note: for testing
        // using the following command to test it:
        // cd karate-grpc
        // mvn exec:java -Dexec.mainClass=com.github.thinkerou.demo.integration.GrpcClientDemo  -Dexec.cleanupDaemonThreads=false
    }

}
