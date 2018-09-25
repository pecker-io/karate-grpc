package com.github.thinkerou.karate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.github.thinkerou.karate.constants.DescriptorFile;
import com.github.thinkerou.karate.message.Output;
import com.github.thinkerou.karate.protobuf.ServiceResolver;
import com.github.thinkerou.karate.utils.Helper;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

/**
 * GrpcList
 *
 * @author thinkerou
 */
public class GrpcList {

    public static String invoke(String serviceFilter, String methodFilter) throws IOException {
        String path = DescriptorFile.PROTO.getText();
        Path descriptorPath = Paths.get(System.getProperty("user.dir") + path);
        Helper.validatePath(Optional.ofNullable(descriptorPath));

        // Fetch the appropriate file descriptors for the service.
        DescriptorProtos.FileDescriptorSet fileDescriptorSet = null;
        try {
            fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(Files.readAllBytes(descriptorPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creates one temp file to save list grpc result.
        Path filePath = null;
        try {
            filePath = Files.createTempFile("karate.grpc.list.", ".result.out");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Helper.validatePath(Optional.ofNullable(filePath));
        System.out.println(filePath);

        Output output = Output.forFile(filePath);
        output.newLine();

        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);

        Iterable<Descriptors.ServiceDescriptor> serviceDescriptorIterable = serviceResolver.listServices();
        serviceDescriptorIterable.forEach(descriptor -> {
            if (serviceFilter.isEmpty()
                    || descriptor.getFullName().toLowerCase().contains(serviceFilter.toLowerCase())) {
                listMethods(output, descriptor, methodFilter);
            }
        });

        return Helper.readFile(filePath.toString());
    }

    private static void listMethods(
            Output output,
            Descriptors.ServiceDescriptor descriptor,
            String methodFilter) {
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
            }

            if (printedService[0]) {
                output.newLine();
            }
        });

        return;
    }

    public static void main(String[] args) throws IOException {
        String result = GrpcList.invoke("Greeter", "SayHello");
        System.out.println(result);
    }

}
