package com.thinkerou.karate;

import static com.thinkerou.karate.utils.Helper.readFile;
import static com.thinkerou.karate.utils.Helper.validatePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.thinkerou.karate.constants.DescriptorFile;
import com.thinkerou.karate.message.Output;
import com.thinkerou.karate.protobuf.ServiceResolver;

/**
 * GrpcList
 *
 * @author thinkerou
 */
public class GrpcList {

    public static String invoke(Optional<String> serviceFilter, Optional<String> methodFilter) {
        String path = DescriptorFile.PROTO.getText();
        Path descriptorPath = Paths.get(System.getProperty("user.dir") + path);
        validatePath(Optional.ofNullable(descriptorPath));

        // Fetch the appropriate file descriptors for the service.
        DescriptorProtos.FileDescriptorSet fileDescriptorSet = null;
        try {
            fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(Files.readAllBytes(descriptorPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fileDescriptorSet);

        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);

        Iterable<Descriptors.ServiceDescriptor> serviceDescriptorIterable = serviceResolver.listServices();
        serviceDescriptorIterable.forEach(descriptor -> {
            if (!serviceFilter.isPresent()
                    || descriptor.getFullName().toLowerCase().contains(serviceFilter.get().toLowerCase())) {
                try {
                    listMethods(descriptor, methodFilter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return "";
    }

    private static String listMethods(
            Descriptors.ServiceDescriptor descriptor,
            Optional<String> methodFilter) throws IOException {
        List<Descriptors.MethodDescriptor> methodDescriptors = descriptor.getMethods();

        // Creates one temp file to save list grpc result.
        Path filePath = null;
        try {
            filePath = Files.createTempFile("karate.grpc.list.", ".result.out");
        } catch (IOException e) {
            e.printStackTrace();
        }
        validatePath(Optional.ofNullable(filePath));
        System.out.println(filePath);

        Output output = Output.forFile(filePath);
        output.newLine();

        final boolean[] printedService = {false};
        methodDescriptors.forEach(method -> {
            if (!methodFilter.isPresent() || method.getName().contains(methodFilter.get())) {
                if (!printedService[0]) {
                    File pFile = new File(descriptor.getFile().getName());
                    output.writeLine(descriptor.getFullName() + " => " + pFile);
                    printedService[0] = true;
                }

                output.writeLine(" " + descriptor.getFullName() + "/" + method.getName());
            }

            if (printedService[0]) {
                output.newLine();
            }
        });

        return readFile(filePath.toString());
    }

    public static void main(String[] args) {
        String result = GrpcList.invoke(Optional.ofNullable("Greeter"), Optional.of("SayHello"));
        System.out.println(result);
        System.out.println("hi");
    }

}
