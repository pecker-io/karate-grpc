package com.github.thinkerou.karate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thinkerou.karate.constants.DescriptorFile;
import com.github.thinkerou.karate.domain.ProtoName;
import com.github.thinkerou.karate.grpc.ChannelFactory;
import com.github.thinkerou.karate.grpc.ComponentObserver;
import com.github.thinkerou.karate.grpc.DynamicClient;
import com.github.thinkerou.karate.grpc.GrpcClientRequestInterceptor;
import com.github.thinkerou.karate.message.Reader;
import com.github.thinkerou.karate.protobuf.ProtoFullName;
import com.github.thinkerou.karate.protobuf.ServiceResolver;
import com.github.thinkerou.karate.utils.FileHelper;
import com.github.thinkerou.karate.utils.RedisHelper;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat.TypeRegistry;
import com.intuit.karate.core.ScenarioBridge;
import com.intuit.karate.core.ScenarioEngine;
import com.intuit.karate.core.Variable;
import com.github.thinkerou.karate.message.Writer;

import io.grpc.CallOptions;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;

/**
 * GrpcCall
 *
 * @author thinkerou
 */
public final class GrpcCall {

    private static final Logger logger = LoggerFactory.getLogger(GrpcCall.class);

    private final String host;
    private final int port;

    /**
     * @param host host
     * @param port port
     * @return GrpcCall
     */
    public static GrpcCall create(String host, int port) {
        return new GrpcCall(host, port);
    }

    /**
     *
     * @param host host
     * @param port port
     */
    public GrpcCall(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @param name    name
     * @param payload payload
     * @return string
     */
    public String invoke(String name, String payload) {
        return execute(name, payload, null, null);
    }

    /**
     * @param name           name
     * @param payload        payload
     * @param scenarioBridge Karate ScenarioBridge
     * @return string
     */
    public String invoke(String name, String payload, ScenarioBridge scenarioBridge) {
        return execute(name, payload, scenarioBridge, null);
    }

    /**
     * @param name        name
     * @param payload     payload
     * @param redisHelper redis helper
     * @return string
     */
    public String invokeByRedis(String name, String payload, ScenarioBridge scenarioBridge, RedisHelper redisHelper) {
        return execute(name, payload, scenarioBridge, redisHelper);
    }

    /**
     * @param name           indicates one called grpc service full name, like:
     *                       package.service/method
     * @param payload        indicates one protobuf corresponding json data
     * @param scenarioBridge the Karate ScenarioBridge
     * @param redisHelper    redis helper
     * @return string
     */
    private String execute(String name, String payload, ScenarioBridge scenarioBridge, RedisHelper redisHelper) {
        ProtoName protoName = ProtoFullName.parse(name);
        byte[] data;
        if (redisHelper != null) {
            data = redisHelper.getDescriptorSets();
        } else {
            String path = System.getProperty("user.home") + DescriptorFile.PROTO_PATH.getText();
            Path descriptorPath = Paths.get(path + DescriptorFile.PROTO_FILE.getText());
            FileHelper.validatePath(Optional.ofNullable(descriptorPath));
            try {
                data = Files.readAllBytes(descriptorPath);
            } catch (IOException e) {
                throw new IllegalArgumentException("Read descriptor path failed: " + descriptorPath.toString());
            }
        }

        // Fetch the appropriate file descriptors for the service.
        FileDescriptorSet fileDescriptorSet;
        try {
            fileDescriptorSet = FileDescriptorSet.parseFrom(data);
        } catch (IOException e) {
            throw new IllegalArgumentException("File descriptor set parse fail: " + e.getMessage());
        }

        // Set up the dynamic client and make the call.
        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        Descriptors.MethodDescriptor methodDescriptor;
        try {
            methodDescriptor = serviceResolver.resolveServiceMethod(protoName);
        } catch (IllegalArgumentException e) {
            // When can't find service or method with name
            // use service or/and method search once for help user
            GrpcList list = new GrpcList();
            String result1 = list.invoke(protoName.getServiceName(), "", false);
            String result2 = list.invoke("", protoName.getMethodName(), false);
            if (!result1.equals("[]") || !result2.equals("[{}]")) {
                logger.warn("Call grpc failed, maybe you should see the follow grpc information.");
                if (!result1.equals("[]")) {
                    logger.info(result1);
                }
                if (!result2.equals("[{}]")) {
                    logger.info(result2);
                }
            }
            throw new IllegalArgumentException(e.getMessage());
        }

        Metadata metadata = new Metadata();
        if (scenarioBridge != null) {
            Variable headers = scenarioBridge.getEngine().getConfig().getHeaders();
            if (headers != null && headers.isMap()) {
                Map<String, String> headerMap = headers.getValue();
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    metadata.put(Metadata.Key.of(entry.getKey(), Metadata.ASCII_STRING_MARSHALLER), entry.getValue());
                }
            }
        }

        ClientInterceptor interceptor = new GrpcClientRequestInterceptor(metadata);
        ManagedChannel channel = ChannelFactory.create(host, port, interceptor);
        // Create a dynamic grpc client.
        DynamicClient dynamicClient = DynamicClient.create(methodDescriptor, channel);

        // This collects all know types into a registry for resolution of potential
        // "Any" types.
        TypeRegistry registry = TypeRegistry.newBuilder().add(serviceResolver.listMessageTypes()).build();

        // Convert payload string to list.
        List<Map<String, Object>> payloadList = new Gson().fromJson(payload, List.class);

        // Need to support stream so it's a list.
        final ImmutableList<DynamicMessage> requestMessages = Reader.create(
                methodDescriptor.getInputType(), payloadList, registry).read();

        // Creates one temp file to save call grpc result.
        Path filePath = null;
        try {
            filePath = Files.createTempFile("karate.grpc.", ".call.result");
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        FileHelper.validatePath(Optional.ofNullable(filePath));

        List<Object> output = new ArrayList<>();
        StreamObserver<DynamicMessage> streamObserver = ComponentObserver.of(Writer.create(output, registry));

        // Calls grpc!
        try {
            dynamicClient.call(requestMessages, streamObserver, callOptions()).get();
        } catch (Throwable t) {
            logger.error(t.getMessage());
            throw new RuntimeException("Caught exception while waiting for rpc", t);
        }

        // Expose response headers to Karate's ScenarioBridge.
        if (scenarioBridge != null) {
            Metadata responseMetadata = ((GrpcClientRequestInterceptor) interceptor).getResponseMetadata();
            Map<String, List<String>> headers = new HashMap();
            responseMetadata.keys().forEach(key -> {
                headers.put(key, ImmutableList.of(responseMetadata.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))));
            });
            scenarioBridge.getEngine().setVariable(ScenarioEngine.RESPONSE_HEADERS, headers);
        }
        return output.toString();
    }

    private static CallOptions callOptions() {
        CallOptions result = CallOptions.DEFAULT;
        // Adds other options parameter
        return result;
    }

}
