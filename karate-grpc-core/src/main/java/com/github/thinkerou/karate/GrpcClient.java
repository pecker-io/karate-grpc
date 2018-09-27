package com.github.thinkerou.karate;

import java.io.IOException;

import com.github.thinkerou.karate.service.GrpcCall;
import com.github.thinkerou.karate.service.GrpcList;
import com.github.thinkerou.karate.utils.Helper;

/**
 * GrpcClient
 *
 * @author thinkerou
 */
public class GrpcClient {

    private static GrpcCall callIns;
    private static GrpcList listIns;

    public static GrpcClient create() {
        listIns = GrpcList.create();
        return new GrpcClient();
    }

    public static GrpcClient create(String host, int port) {
        callIns = GrpcCall.create(host, port);
        return new GrpcClient();
    }

    public String call(String name, String payload) throws IOException {
        return callIns.invoke(name, payload);
    }

    public String list(String serviceFilter, String methodFilter) throws IOException {
        return listIns.invoke(serviceFilter, methodFilter);
    }

    public static void main(String[] args) throws IOException {
        // Note: for testing
        // Need to copy karate-grpc-demo/target/generated-resources to karate-grpc-core/target
        GrpcClient client1 = GrpcClient.create();
        String result1 = client1.list("Greeter", "SayHello");
        System.out.println(result1);

        String file = System.getProperty("user.dir") + "/../karate-grpc-demo/src/test/java/demo/helloworld/helloworld.json";
        System.out.println(file);
        String payloads = Helper.readFile(file);
        System.out.println(payloads);

        // Note: for testing
        // Need to start helloworld server first
        // using the following command:
        //   cd karate-grpc-demo
        //   mvn exec:java -Dexec.mainClass=com.github.thinkerou.demo.helloworld.HelloWorldServer
        GrpcClient client2 = GrpcClient.create("localhost", 50051);
        String result2 = client2.call("helloworld.Greeter/SayHello", payloads);
        System.out.println(result2);

        // Note: for testing
        // using the following command to test it:
        // cd karate-grpc-core
        // mvn exec:java -Dexec.mainClass=com.github.thinkerou.karate.GrpcClient  -Dexec.cleanupDaemonThreads=false
    }

}
