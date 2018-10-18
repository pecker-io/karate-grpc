package com.github.thinkerou.demo.integration;

import java.util.logging.Logger;

import com.github.thinkerou.karate.service.GrpcCall;
import com.github.thinkerou.karate.utils.FileHelper;

/**
 * GrpcCallDemo
 *
 * @author thinkerou
 */
public class GrpcCallDemo {

    private static final Logger logger = Logger.getLogger(GrpcCallDemo.class.getName());

    public static void main(String[] args) throws Exception {
        GrpcCall client = GrpcCall.create("localhost", 50051);

        String file = System.getProperty("user.dir") + "/src/test/java/demo/helloworld/helloworld.json";
        String payloads = FileHelper.readFile(file);
        logger.info(payloads);

        String result = client.invoke("helloworld.Greeter/SayHello", payloads);
        logger.info(result);

        // Note: for testing
        // using the following command to test it:
        // cd karate-grpc
        // mvn exec:java -Dexec.mainClass=com.github.thinkerou.demo.integration.GrpcCallDemo  -Dexec.cleanupDaemonThreads=false
    }

}
