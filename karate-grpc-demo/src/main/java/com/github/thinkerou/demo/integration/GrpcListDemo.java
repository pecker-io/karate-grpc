package com.github.thinkerou.demo.integration;

import java.io.IOException;
import java.util.logging.Logger;

import com.github.thinkerou.karate.service.GrpcList;

/**
 * GrpcListDemo
 *
 * @author thinkerou
 */
public class GrpcListDemo {

    private static final Logger logger = Logger.getLogger(GrpcListDemo.class.getName());

    public static void main(String[] args) throws IOException {
        GrpcList list = new GrpcList();
        String result = list.invoke("Greeter", "SayHello", true);
        logger.info(result);

        result = list.invoke("helloworld.Greeter/SayHello", true);
        logger.info(result);

        // Note: for testing
        // using the following command to test it:
        // cd karate-grpc
        // mvn exec:java -Dexec.mainClass=com.github.thinkerou.demo.integration.GrpcListDemo  -Dexec.cleanupDaemonThreads=false
    }

}
