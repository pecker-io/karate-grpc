package com.github.thinkerou.karate.service;

import static com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import static com.google.protobuf.util.JsonFormat.TypeRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.github.thinkerou.karate.constants.DescriptorFile;
import com.github.thinkerou.karate.domain.ProtoName;
import com.github.thinkerou.karate.grpc.ChannelFactory;
import com.github.thinkerou.karate.grpc.ComponentObserver;
import com.github.thinkerou.karate.grpc.DynamicClient;
import com.github.thinkerou.karate.message.Reader;
import com.github.thinkerou.karate.protobuf.ProtoFullName;
import com.github.thinkerou.karate.protobuf.ServiceResolver;
import com.github.thinkerou.karate.utils.Helper;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.github.thinkerou.karate.message.Writer;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/**
 * GrpcCall
 *
 * @author thinkerou
 */
public class GrpcCall {

    private static final Logger logger = Logger.getLogger(GrpcCall.class.getName());

    private Channel channel;

    public static GrpcCall create(String host, int port) {
        return new GrpcCall(host, port);
    }

    public GrpcCall(String host, int port) {
        channel = ChannelFactory.create(host, port);
    }

    /**
     * @param name indicates one called grpc service full name, like: package.service/method
     * @param payload indicates one protobuf corresponding json data
     */
    public String invoke(String name, String payload) throws IOException {
        ProtoName protoName = ProtoFullName.parse(name);

        String path = DescriptorFile.PROTO.getText();
        Path descriptorPath = Paths.get(System.getProperty("user.dir") + path);
        Helper.validatePath(Optional.ofNullable(descriptorPath));

        // Fetch the appropriate file descriptors for the service.
        FileDescriptorSet fileDescriptorSet = null;
        try {
            fileDescriptorSet = FileDescriptorSet.parseFrom(Files.readAllBytes(descriptorPath));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }

        // Set up the dynamic client and make the call.
        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        Descriptors.MethodDescriptor methodDescriptor = null;
        try {
            methodDescriptor = serviceResolver.resolveServiceMethod(protoName);
        } catch (IllegalArgumentException e) {
            // When can't find service or method with name
            // use service or/and method search once for help user
            logger.warning("Call grpc failed, maybe you should see the follow grpc information.");
            GrpcList list = new GrpcList();
            String result = list.invoke(protoName.getServiceName(), "", false);
            logger.info(result);
            result = list.invoke("", protoName.getMethodName(), false);
            logger.info(result);
            throw new IllegalArgumentException(e.getMessage());
        }

        // Create a dynamic grpc client.
        DynamicClient dynamicClient = DynamicClient.create(methodDescriptor, channel);

        // This collects all know types into a registry for resolution of potential "Any" types.
        TypeRegistry registry = TypeRegistry.newBuilder().add(serviceResolver.listMessageTypes()).build();

        // Convert payload string to list.
        List<Map<String, Object>> payloadList = new Gson().fromJson(payload, List.class);

        // Need to support stream so it's a list.
        final ImmutableList<DynamicMessage> requestMessages = Reader.create(
                methodDescriptor.getInputType(), payloadList, registry
        ).read();

        // Creates one temp file to save call grpc result.
        Path filePath = null;
        try {
            filePath = Files.createTempFile("karate.grpc.", ".call.result");
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        Helper.validatePath(Optional.ofNullable(filePath));

        List<Object> output = new ArrayList<>();
        StreamObserver<DynamicMessage> streamObserver = ComponentObserver.of(Writer.create(output, registry));

        // Calls grpc!
        try {
            dynamicClient.call(requestMessages, streamObserver, callOptions()).get();
        } catch (Throwable t) {
            throw new RuntimeException("Caught exception while waiting for rpc", t);
        }

        return output.toString();
    }

    private static CallOptions callOptions() {
        CallOptions result = CallOptions.DEFAULT;
        // Adds other options parameter
        return result;
    }

}
