package com.github.thinkerou.karate.protobuf;

import com.google.protobuf.Descriptors.MethodDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ProtocolStringList;
import com.github.thinkerou.karate.domain.ProtoName;

/**
 * ServiceResolver
 *
 * @author thinkerou
 */
public final class ServiceResolver {

    private static final Logger logger = Logger.getLogger(ServiceResolver.class.getName());

    private final ImmutableList<Descriptors.FileDescriptor> fileDescriptors;

    /**
     * Creates a resolver which searches the supplied FileDescriptorSet.
     * @param descriptorSet descriptorSet
     * @return ServiceResolver
     */
    public static ServiceResolver fromFileDescriptorSet(DescriptorProtos.FileDescriptorSet descriptorSet) {
        ImmutableMap<String, DescriptorProtos.FileDescriptorProto> descriptorProtoIndex =
                computeDescriptorProtoIndex(descriptorSet);

        Map<String, Descriptors.FileDescriptor> descriptorCache = new HashMap<>();

        ImmutableList.Builder<Descriptors.FileDescriptor> result = ImmutableList.builder();
        List<DescriptorProtos.FileDescriptorProto> descriptorProtos = descriptorSet.getFileList();
        for (DescriptorProtos.FileDescriptorProto descriptorProto : descriptorProtos) {
            try {
                result.add(descriptorFromProto(descriptorProto, descriptorProtoIndex, descriptorCache));
            } catch (Descriptors.DescriptorValidationException e) {
                logger.warning(e.getMessage());
            }
        }

        return new ServiceResolver(result.build());
    }

    /**
     * Lists all the services found in the file descriptors.
     *
     * @return Iterable
     */
    public Iterable<Descriptors.ServiceDescriptor> listServices() {
        ArrayList<Descriptors.ServiceDescriptor> serviceDescriptors = new ArrayList<>();
        fileDescriptors.forEach(fileDescriptor -> serviceDescriptors.addAll(fileDescriptor.getServices()));

        return serviceDescriptors;
    }

    /**
     * Lists all the known message types.
     *
     * @return ImmutableSet
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
     *
     * @param method method
     * @return MethodDescriptor
     */
    public Optional<MethodDescriptor> resolveServiceMethod(ProtoName method) {
        return resolveServiceMethod(
                method.getServiceName(),
                method.getMethodName(),
                method.getPackageName());
    }

    private Optional<MethodDescriptor> resolveServiceMethod(
            String serviceName, String methodName, String packageName) {
        return findService(serviceName, packageName)
            .map(serviceDescriptor -> serviceDescriptor.findMethodByName(methodName));
    }

    private Optional<Descriptors.ServiceDescriptor> findService(String serviceName, String packageName) {
        return getFileDescriptors().stream()
            .filter(each -> each.getPackage().equals(packageName))
            .map(each -> each.findServiceByName(serviceName))
            .filter(Objects::nonNull)
            .findFirst();
    }

    private synchronized  ImmutableList<Descriptors.FileDescriptor> getFileDescriptors() {
        if (fileDescriptors.isEmpty()) {
            listServices();
        }
        return fileDescriptors;
    }

    /**
     * Returns a map from descriptor proto name as found inside the descriptors to protos.
     */
    private static ImmutableMap<String, DescriptorProtos.FileDescriptorProto> computeDescriptorProtoIndex(
            DescriptorProtos.FileDescriptorSet fileDescriptorSet) {
        ImmutableMap.Builder<String, DescriptorProtos.FileDescriptorProto> resultBuilder = ImmutableMap.builder();

        List<DescriptorProtos.FileDescriptorProto> descriptorProtos = fileDescriptorSet.getFileList();
        descriptorProtos.forEach(descriptorProto -> resultBuilder.put(descriptorProto.getName(), descriptorProto));

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
        String descriptorName = descriptorProto.getName();
        if (descriptorCache.containsKey(descriptorName)) {
            return descriptorCache.get(descriptorName);
        }

        // Then, fetch all the required dependencies recursively.
        ImmutableList.Builder<Descriptors.FileDescriptor> dependencies = ImmutableList.builder();
        ProtocolStringList protocolStringList = descriptorProto.getDependencyList();
        protocolStringList.forEach(dependencyName -> {
            if (!descriptorProtoIndex.containsKey(dependencyName)) {
                throw new IllegalArgumentException("Can't find dependency: " + dependencyName);
            }
            DescriptorProtos.FileDescriptorProto dependencyProto = descriptorProtoIndex.get(dependencyName);
            try {
                dependencies.add(descriptorFromProto(dependencyProto, descriptorProtoIndex, descriptorCache));
            } catch (Descriptors.DescriptorValidationException e) {
                logger.warning(e.getMessage());
            }
        });

        // Finally, construct the actual descriptor.
        Descriptors.FileDescriptor[] empty = new Descriptors.FileDescriptor[0];
        Descriptors.FileDescriptor descriptor = Descriptors.FileDescriptor.buildFrom(descriptorProto, dependencies.build().toArray(empty));
        descriptorCache.put(descriptorName, descriptor);
        
        return descriptor;
    }

}
