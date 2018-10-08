package com.github.thinkerou.karate.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.github.thinkerou.karate.constants.DescriptorFile;
import com.github.thinkerou.karate.domain.ProtoName;
import com.github.thinkerou.karate.message.Output;
import com.github.thinkerou.karate.protobuf.ProtoFullName;
import com.github.thinkerou.karate.protobuf.ServiceResolver;
import com.github.thinkerou.karate.utils.Helper;
import com.google.common.base.Joiner;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

/**
 * GrpcList
 *
 * Utility to list the services, methods and message definitions for the known grpc end-points.
 *
 * @author thinkerou
 */
public class GrpcList {

    private static final Logger logger = Logger.getLogger(GrpcList.class.getName());

    public static GrpcList create() {
        return new GrpcList();
    }

    public GrpcList() {
    }

    /**
     * Support format: packageName.serviceName/methodName
     */
    public String invoke(String name, Boolean withMessage) throws IOException {
        ProtoName protoName = ProtoFullName.parse(name);
        return invoke(protoName.getServiceName(), protoName.getMethodName(), withMessage);
    }

    /**
     * List the grpc services filtered by service name (contains) or method name (contains).
     */
    public String invoke(String serviceFilter, String methodFilter, Boolean withMessage) throws IOException {
        String path = DescriptorFile.PROTO.getText();
        Path descriptorPath = Paths.get(System.getProperty("user.dir") + path);
        Helper.validatePath(Optional.ofNullable(descriptorPath));

        // Fetch the appropriate file descriptors for the service.
        DescriptorProtos.FileDescriptorSet fileDescriptorSet = null;
        try {
            fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(Files.readAllBytes(descriptorPath));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }

        // Creates one temp file to save list grpc result.
        Path filePath = null;
        try {
            filePath = Files.createTempFile("karate.grpc.", ".list.result");
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        Helper.validatePath(Optional.ofNullable(filePath));
        logger.info(filePath.toString());

        Output output = Output.forFile(filePath);
        output.newLine();

        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);

        Iterable<Descriptors.ServiceDescriptor> serviceDescriptorIterable = serviceResolver.listServices();
        serviceDescriptorIterable.forEach(descriptor -> {
            if (serviceFilter.isEmpty()
                    || descriptor.getFullName().toLowerCase().contains(serviceFilter.toLowerCase())) {
                listMethods(output, descriptor, methodFilter, withMessage);
            }
        });

        return Helper.readFile(filePath.toString());
    }

    /**
     * List the methods on the service (the mothodFilter will be applied if non empty.
     */
    private static void listMethods(
            Output output,
            Descriptors.ServiceDescriptor descriptor,
            String methodFilter,
            Boolean withMessage) {
        List<Descriptors.MethodDescriptor> methodDescriptors = descriptor.getMethods();

        final boolean[] printedService = {false};
        methodDescriptors.forEach(method -> {
            if (methodFilter.isEmpty() || method.getName().contains(methodFilter)) {
                if (!printedService[0]) {
                    File pFile = new File(descriptor.getFile().getName());
                    output.writeLine(descriptor.getFullName() + " => " + pFile);
                    printedService[0] = true;
                }

                output.writeLine(" " + descriptor.getFullName() + "/" + method.getName());

                // If requested, add the message definition
                if (withMessage) {
                    output.writeLine(renderDescriptor(method.getInputType(), "  "));
                    output.newLine();
                }
            }

            if (printedService[0]) {
                output.newLine();
            }
        });
    }

    /**
     * Create a human readable string to help the user build a message to send to an end-point.
     */
    private static String renderDescriptor(Descriptors.Descriptor descriptor, String indent) {
        if (descriptor.getFields().size() == 0) {
            return indent + "<empty>";
        }

        List<String> fieldsAsStrings = descriptor.getFields().stream()
                .map(field -> renderDescriptor(field, indent + "  "))
                .collect(Collectors.toList());

        return Joiner.on(System.lineSeparator()).join(fieldsAsStrings);
    }

    /**
     * Create a readable string from the field to help the user build a message.
     */
    private static String renderDescriptor(Descriptors.FieldDescriptor descriptor, String indent) {
        String isOpt = descriptor.isOptional() ? "<optional>" : "<required>";
        String isRep = descriptor.isRepeated() ? "<repeated>" : "<single>";
        String fieldPrefix = indent + descriptor.getJsonName() + "[" + isOpt + " " + isRep + "]";

        if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            return fieldPrefix + " {" + System.lineSeparator()
                    + renderDescriptor(descriptor.getMessageType(), indent + "  ")
                    + System.lineSeparator() + indent + "}";
        }

        if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
            return fieldPrefix + ": " + descriptor.getEnumType().getValues();
        }

        return fieldPrefix + ": " + descriptor.getJavaType();
    }

}
