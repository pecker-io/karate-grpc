package com.thinkerou.karate;

import static com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import static com.google.protobuf.util.JsonFormat.TypeRegistry;
import static com.thinkerou.karate.utils.Helper.readFile;
import static com.thinkerou.karate.utils.Helper.validatePath;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.thinkerou.karate.domain.ProtoName;
import com.thinkerou.karate.grpc.ChannelFactory;
import com.thinkerou.karate.grpc.CompositeStreamObserver;
import com.thinkerou.karate.grpc.DynamicClient;
import com.thinkerou.karate.message.Output;
import com.thinkerou.karate.message.Reader;
import com.thinkerou.karate.message.Writer;
import com.thinkerou.karate.protobuf.FullName;
import com.thinkerou.karate.protobuf.ServiceResolver;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/**
 * GrpcClient
 *
 * @author thinkerou
 */
public class GrpcClient {

    private Channel channel;

    public static GrpcClient create(String host, int port) {
        return new GrpcClient(host, port);
    }

    GrpcClient(String host, int port) {
        channel = ChannelFactory.create(host, port);
    }

    /**
     * @param name indicates one called grpc service full name, like: package.service/method
     * @param payload indicates one protobuf corresponding json data
     */
    public String invoke(String name, String payload) throws IOException {
        ProtoName protoName = FullName.parse(name);

        String path = "/target/generated-resources/protobuf/descriptor-sets/karate-grpc.protobin";
        Path descriptorPath = Paths.get(System.getProperty("user.dir") + path);
        validatePath(Optional.ofNullable(descriptorPath));

        // Fetch the appropriate file descriptors for the service.
        FileDescriptorSet fileDescriptorSet = null;
        try {
            fileDescriptorSet = FileDescriptorSet.parseFrom(Files.readAllBytes(descriptorPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up the dynamic client and make the call.
        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        Descriptors.MethodDescriptor methodDescriptor = serviceResolver.resolveServiceMethod(protoName);

        // Create a dynamic grpc client.
        DynamicClient dynamicClient = DynamicClient.create(methodDescriptor, channel);

        // This collects all know types into a registry for resolution of potential "Any" types.
        TypeRegistry registry = TypeRegistry.newBuilder().add(serviceResolver.listMessageTypes()).build();

        // Convert payload string to list.
        Gson gson = new Gson();
        List<Map<String, Object>> payloadList = gson.fromJson(payload, List.class);

        // Need to support stream so it's a list.
        final ImmutableList<DynamicMessage> requestMessages = Reader.create(
                methodDescriptor.getInputType(), payloadList, registry
        ).read();

        // Creates one temp file to save call grpc result.
        Path filePath = null;
        try {
            filePath = Files.createTempFile("karate.grpc.", ".result.out");
        } catch (IOException e) {
            e.printStackTrace();
        }
        validatePath(Optional.ofNullable(filePath));

        Output output = Output.forFile(filePath);

        StreamObserver<DynamicMessage> streamObserver = CompositeStreamObserver.of(Writer.create(output, registry));

        // Calls grpc!
        try {
            dynamicClient.call(requestMessages, streamObserver, callOptions()).get();
        } catch (Throwable t) {
            throw new RuntimeException("Caught exception while waiting for rpc", t);
        }

        return readFile(filePath.toString());
    }

    private static CallOptions callOptions() {
        CallOptions result = CallOptions.DEFAULT;
        // Adds other options parameter
        return result;
    }

    // Test it.
    public static void main(String[] args) throws Exception {
        GrpcClient client = GrpcClient.create("localhost", 50051);

        String file = System.getProperty("user.dir") + "/src/test/java/demo/helloworld/helloworld.json";
        String payloads = readFile(file);
        System.out.println(payloads);

        String result = client.invoke("helloworld.Greeter/SayHello", payloads);
        System.out.println(result);
    }

}
