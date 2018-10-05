package com.github.thinkerou.demo.integration;

import java.io.IOException;
import java.util.logging.Logger;

import com.github.thinkerou.karate.GrpcClient;
import com.github.thinkerou.karate.utils.Helper;

/**
 * GrpcClientDemo
 *
 * @author thinkerou
 */
public class GrpcClientDemo {

    private static final Logger logger = Logger.getLogger(GrpcClientDemo.class.getName());

    public static void main(String[] args) throws IOException {
        GrpcClient client1 = GrpcClient.create();
        String result1 = client1.list("Greeter", "SayHello");
        logger.info(result1);
        result1 = client1.list("helloworld.Greeter/SayHello");
        logger.info(result1);

        String file = System.getProperty("user.dir") + "/src/test/java/demo/helloworld/helloworld.json";
        logger.info(file);
        String payloads = Helper.readFile(file);
        logger.info(payloads);

        // Note: for testing
        // Need to start helloworld server first
        // using the following command:
        //   cd karate-demo
        //   mvn exec:java -Dexec.mainClass=com.github.thinkerou.demo.helloworld.HelloWorldServer
        GrpcClient client2 = GrpcClient.create("localhost", 50051);
        String result2 = client2.call("helloworld.Greeter/SayHello", payloads);
        logger.info(result2);

        // Note: for testing
        // using the following command to test it:
        // cd karate-grpc
        // mvn exec:java -Dexec.mainClass=com.github.thinkerou.demo.integration.GrpcClientDemo  -Dexec.cleanupDaemonThreads=false
    }

}
