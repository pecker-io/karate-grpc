package com.github.thinkerou.karate.service;

import static com.google.protobuf.util.JsonFormat.TypeRegistry;

import com.github.thinkerou.karate.domain.ProtoName;
import com.github.thinkerou.karate.grpc.ChannelFactory;
import com.github.thinkerou.karate.grpc.ComponentObserver;
import com.github.thinkerou.karate.grpc.DynamicClient;
import com.github.thinkerou.karate.message.Reader;
import com.github.thinkerou.karate.message.Writer;
import com.github.thinkerou.karate.protobuf.ProtoFullName;
import com.github.thinkerou.karate.protobuf.ServiceResolver;
import com.github.thinkerou.karate.utils.DataReader;
import com.github.thinkerou.karate.utils.FileHelper;
import com.github.thinkerou.karate.utils.RedisHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * GrpcCall
 *
 * @author thinkerou
 */
public final class GrpcCall {

    private static final Logger logger = Logger.getLogger(GrpcCall.class.getName());

    private final ManagedChannel channel;

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
        channel = ChannelFactory.create(host, port);
    }

    /**
     * @param name name
     * @param payload payload
     * @return string
     */
    public String invoke(String name, String payload) {
        return execute(name, payload, null);
    }

    /**
     * @param name name
     * @param payload payload
     * @param redisHelper redis helper
     * @return string
     */
    public String invokeByRedis(String name, String payload, RedisHelper redisHelper) {
        return execute(name, payload, redisHelper);
    }

    /**
     * @param name indicates one called grpc service full name, like: package.service/method
     * @param payload indicates one protobuf corresponding json data
     * @param redisHelper redis helper
     * @return string
     */
    private String execute(String name, String payload, RedisHelper redisHelper) {
        ProtoName protoName = ProtoFullName.parse(name);

        Pair<ServiceResolver, Optional<MethodDescriptor>> found =
            DataReader.read(redisHelper)
                .stream()
                .map(each -> new Pair<>(each, each.resolveServiceMethod(protoName)))
                .filter(each -> each.right().isPresent())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Service Resolver Not found"));

        Descriptors.MethodDescriptor methodDescriptor = found.right()
            .orElseThrow(() -> {
                // When can't find service or method with name
                // use service or/and method search once for help user
                GrpcList list = new GrpcList();
                String result1 = list.invoke(protoName.getServiceName(), "", false);
                String result2 = list.invoke("", protoName.getMethodName(), false);
                if (!result1.equals("[]") || !result2.equals("[{}]")) {
                    logger.warning(
                        "Call grpc failed, maybe you should see the follow grpc information.");
                    if (!result1.equals("[]")) {
                        logger.info(result1);
                    }
                    if (!result2.equals("[{}]")) {
                        logger.info(result2);
                    }
                }
                String text = "Can't find method " + protoName.getMethodName()
                    + " in service " + protoName.getServiceName();
                return new IllegalArgumentException(text);
            });

        // Set up the dynamic client and make the call.

        // Create a dynamic grpc client.
        DynamicClient dynamicClient = DynamicClient.create(methodDescriptor, channel);

        // This collects all know types into a registry for resolution of potential "Any" types.
        TypeRegistry registry = TypeRegistry.newBuilder().add(found.left().listMessageTypes()).build();

        // Need to support stream so it's a list.
        final ImmutableList<DynamicMessage> requestMessages =
            extractRequestMessages(payload, methodDescriptor, registry);

        // Creates one temp file to save call grpc result.
        Path filePath = null;
        try {
            filePath = Files.createTempFile("karate.grpc.", ".call.result");
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        FileHelper.validatePath(Optional.ofNullable(filePath));
        StreamObserver<DynamicMessage> streamObserver;
        List<Object> output = new ArrayList<>();
            streamObserver = ComponentObserver.of(Writer.create(output, registry));

        // Calls grpc!
        try {
            Objects.requireNonNull(
                dynamicClient.call(requestMessages, streamObserver, callOptions())).get();
        } catch (Throwable t) {
            throw new RuntimeException("Caught exception while waiting for rpc", t);
        }
        if (dynamicClient.getMethodType() == MethodType.UNARY ||
        dynamicClient.getMethodType() == MethodType.CLIENT_STREAMING) {
            return output.get(0).toString();
        }
        return output.toString();
    }

    private static CallOptions callOptions() {
        CallOptions result = CallOptions.DEFAULT;
        // Adds other options parameter
        return result;
    }

    private static ImmutableList<DynamicMessage> extractRequestMessages(String raw,
        Descriptors.MethodDescriptor methodDescriptor, TypeRegistry registry) {
        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        try {
            List<Map<String, Object>> payloads = new Gson().fromJson(raw, type);
            return Reader.create(methodDescriptor.getInputType(), payloads, registry).read();
        } catch (JsonSyntaxException ignored) {
            type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> payload = new Gson().fromJson(raw, type);
            return Reader.create(methodDescriptor.getInputType(), payload, registry).read();
        }
    }

}
