package com.github.thinkerou.karate.service;

import com.github.thinkerou.karate.domain.ProtoName;
import com.github.thinkerou.karate.protobuf.ProtoFullName;
import com.github.thinkerou.karate.utils.DataReader;
import com.github.thinkerou.karate.utils.RedisHelper;
import com.google.gson.Gson;
import com.google.protobuf.Descriptors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * GrpcList
 * Utility to list the services, methods and message definitions for the known grpc end-points.
 *
 * @author thinkerou
 */
public final class GrpcList {

    /**
     * @return grpc list
     */
    public static GrpcList create() {
        return new GrpcList();
    }

    public GrpcList() {
    }

    /**
     * Support format: packageName.serviceName/methodName
     * @param name name
     * @param withMessage withMessage boolean
     * @return List the grpc services filtered by params.
     */
    public String invoke(String name, Boolean withMessage) {
        return new Gson().toJson(execute(name, withMessage, null));
    }

    /**
     * @param service service
     * @param method method
     * @param withMessage withMessage boolean
     * @return List the grpc services filtered by params.
     */
    public String invoke(String service, String method, Boolean withMessage) {
        return new Gson().toJson(execute(service, method, withMessage, false, null));
    }

    /**
     * @param name name
     * @param withMessage withMessage boolean
     * @param redisHelper redis helper to use for invocation
     * @return List the grpc services filtered by params.
     */
    public String invokeByRedis(String name, Boolean withMessage, RedisHelper redisHelper) {
        return new Gson().toJson(execute(name, withMessage, redisHelper));
    }

    /**
     * @param service service
     * @param method method
     * @param withMessage withMessage boolean
     * @param redisHelper redis helper to use for invocation
     * @return List the grpc services filtered by params.
     */
    public String invokeByRedis(String service, String method, Boolean withMessage, RedisHelper redisHelper) {
        return new Gson().toJson(execute(service, method, withMessage, false, redisHelper));
    }

    private List<Map<String, Object>> execute(String name, Boolean withMessage, RedisHelper redisHelper) {
        ProtoName protoName = ProtoFullName.parse(name);
        return execute(protoName.getServiceName(), protoName.getMethodName(), withMessage, false, redisHelper);
    }

    /**
     * List the grpc services filtered by service name (contains) or method name (contains).
     * Mainly goal: return value are used web page.
     */
    private List<Map<String, Object>> execute(
            String serviceFilter,
            String methodFilter,
            Boolean withMessage,
            Boolean saveOutput,
            RedisHelper redisHelper) {

        List<Map<String, Object>> output = new ArrayList<>();
        DataReader.read(redisHelper)
            .stream()
            .flatMap(each -> StreamSupport.stream(each.listServices().spliterator(), false))
            .forEach(
                descriptor -> {
                    if (serviceFilter.isEmpty()
                        || descriptor.getFullName().toLowerCase()
                        .contains(serviceFilter.toLowerCase())) {
                        Map<String, Object> result = new HashMap<>();
                        listMethods(result, descriptor, methodFilter, withMessage, saveOutput);

                        if (!result.isEmpty()) {
                            output.add(result);
                        }
                    }

                });
        return output;
    }

    /**
     * List the methods on the service (the mothodFilter will be applied if non empty.
     */
    private static void listMethods(
            Map<String, Object> output,
            Descriptors.ServiceDescriptor descriptor,
            String methodFilter,
            Boolean withMessage,
            Boolean saveOutputInfo) {
        List<Descriptors.MethodDescriptor> methodDescriptors = descriptor.getMethods();

        methodDescriptors.forEach(method -> {
            if (methodFilter.isEmpty() || method.getName().contains(methodFilter)) {
                String key = descriptor.getFullName() + "/" + method.getName();

                Map<String, Object> res = new HashMap<>();
                res.put("file", descriptor.getFile().getName());

                // If requested, add the message definition
                if (withMessage) {
                    Map<String, Object> o = new HashMap<>();
                    o.put(method.getInputType().getName(), renderDescriptor(method.getInputType()));
                    res.put("input", o);
                    if (saveOutputInfo) {
                        Map<String, Object> oo = new HashMap<>();
                        oo.put(method.getOutputType().getName(), renderDescriptor(method.getOutputType()));
                        res.put("output", oo);
                    }
                }
                output.put(key, res);
            }
        });
    }

    /**
     * Create a human readable string to help the user build a message to send to an end-point.
     */
    private static Map<String, Object> renderDescriptor(Descriptors.Descriptor descriptor) {
        Map<String, Object> result = new HashMap<>();

        if (descriptor.getFields().size() == 0) {
            result.put("EMPTY", "");
            return result;
        }

        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            String isOpt = field.isOptional() ? "OPTIONAL" : "REQUIRED";
            String isRep = field.isRepeated() ? "REPEATED" : "SINGLE";
            String fieldPrefix = field.getJsonName() + "." + isOpt + "." + isRep;
            result.put(fieldPrefix, renderFieldDescriptor(field));
        }

        return result;
    }

    /**
     * Create a readable string from the field to help the user build a message.
     */
    private static Object renderFieldDescriptor(Descriptors.FieldDescriptor descriptor) {
        switch (descriptor.getJavaType()) {
            case MESSAGE:
                return renderDescriptor(descriptor.getMessageType());
            case ENUM:
                return descriptor.getEnumType().getValues();
            default:
                return descriptor.getJavaType();
        }
    }
}
