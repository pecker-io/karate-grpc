package com.thinkerou.karate.protobuf;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.thinkerou.karate.domain.ProtoName;

/**
 * ServiceResolver
 *
 * @author thinkerou
 */
public class ServiceResolver {

    private final ImmutableList<Descriptors.FileDescriptor> fileDescriptors;

    /**
     * Creates a resolver which searches the supplied FileDescriptorSet.
     */
    public static ServiceResolver fromFileDescriptorSet(DescriptorProtos.FileDescriptorSet descriptorSet) {
        ImmutableMap<String, DescriptorProtos.FileDescriptorProto> descriptorProtoIndex =
                computeDescriptorProtoIndex(descriptorSet);

        Map<String, Descriptors.FileDescriptor> descriptorCache = new HashMap<>();

        ImmutableList.Builder<Descriptors.FileDescriptor> result = ImmutableList.builder();
        for (DescriptorProtos.FileDescriptorProto descriptorProto : descriptorSet.getFileList()) {
            try {
                result.add(descriptorFromProto(descriptorProto, descriptorProtoIndex, descriptorCache));
            } catch (Descriptors.DescriptorValidationException e) {
                e.printStackTrace();
                continue;
            }
        }

        return new ServiceResolver(result.build());
    }

    /**
     * Lists all the known message types.
     */
    public ImmutableSet<Descriptors.Descriptor> listMessageTypes() {
        ImmutableSet.Builder<Descriptors.Descriptor> resultBuilder = ImmutableSet.builder();
        fileDescriptors.forEach(d -> resultBuilder.addAll(d.getMessageTypes()));

        return resultBuilder.build();
    }

    private ServiceResolver(Iterable<Descriptors.FileDescriptor> fileDescriptors) {
        this.fileDescriptors = ImmutableList.copyOf(fileDescriptors);
    }

    /**
     * Returns the descriptor of a protobuf method with the supplied grpc method name.
     * If the method can't be found, this throw IllegalArgumentException.
     */
    public Descriptors.MethodDescriptor resolveServiceMethod(ProtoName method) {
        return resolveServiceMethod(
                method.getServiceName(),
                method.getMethodName(),
                method.getPackageName());
    }

    private Descriptors.MethodDescriptor resolveServiceMethod(
            String serviceName, String methodName, String packageName) {
        Descriptors.ServiceDescriptor service = findService(serviceName, packageName);
        Descriptors.MethodDescriptor method = service.findMethodByName(methodName);
        if (method == null) {
            throw new IllegalArgumentException(
                    "Can't find method " + methodName + " in service " + serviceName);
        }

        return method;
    }

    private Descriptors.ServiceDescriptor findService(String serviceName, String packageName) {
        for (Descriptors.FileDescriptor fileDescriptor : fileDescriptors) {
            if (!fileDescriptor.getPackage().equals(packageName)) {
                // Package does not match this file, ignore.
                continue;
            }

            Descriptors.ServiceDescriptor serviceDescriptor = fileDescriptor.findServiceByName(serviceName);
            if (serviceDescriptor != null) {
                return serviceDescriptor;
            }
        }
        throw new IllegalArgumentException("Can't find service with name: " + serviceName);
    }

    /**
     * Returns a map from descriptor proto name as found inside the descriptors to protos.
     */
    private static ImmutableMap<String, DescriptorProtos.FileDescriptorProto> computeDescriptorProtoIndex(
            DescriptorProtos.FileDescriptorSet fileDescriptorSet) {
        ImmutableMap.Builder<String, DescriptorProtos.FileDescriptorProto> resultBuilder = ImmutableMap.builder();

        for (DescriptorProtos.FileDescriptorProto descriptorProto : fileDescriptorSet.getFileList()) {
            resultBuilder.put(descriptorProto.getName(), descriptorProto);
        }

        return resultBuilder.build();
    }

    /**
     * Recursively constructs file descriptors for all dependencies of the supplied proto and
     * returns a FileDescriptor for the supplied proto itself.
     * For maximal efficientcy, reuse the descriptorCache argument across calls.
     */
    private static Descriptors.FileDescriptor descriptorFromProto(
            DescriptorProtos.FileDescriptorProto descriptorProto,
            ImmutableMap<String, DescriptorProtos.FileDescriptorProto> descriptorProtoIndex,
            Map<String, Descriptors.FileDescriptor> descriptorCache) throws Descriptors.DescriptorValidationException {
        // First, check the cache.
        String descritorName = descriptorProto.getName();
        if (descriptorCache.containsKey(descritorName)) {
            return descriptorCache.get(descritorName);
        }

        // Then, fetch all the required dependencies recursively.
        ImmutableList.Builder<Descriptors.FileDescriptor> dependencies = ImmutableList.builder();
        for (String dependencyName : descriptorProto.getDependencyList()) {
            if (!descriptorProtoIndex.containsKey(dependencyName)) {
                throw new IllegalArgumentException("Can't find dependency: " + dependencyName);
            }
            DescriptorProtos.FileDescriptorProto dependencyProto = descriptorProtoIndex.get(dependencyName);
            dependencies.add(descriptorFromProto(dependencyProto, descriptorProtoIndex, descriptorCache));
        }

        // Finally, construct the actual descriptor.
        Descriptors.FileDescriptor[] empty = new Descriptors.FileDescriptor[0];

        return Descriptors.FileDescriptor.buildFrom(descriptorProto, dependencies.build().toArray(empty));
    }

}
