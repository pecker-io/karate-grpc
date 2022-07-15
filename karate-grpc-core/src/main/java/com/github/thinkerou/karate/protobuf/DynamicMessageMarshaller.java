package com.github.thinkerou.karate.protobuf;

import java.io.IOException;
import java.io.InputStream;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.ExtensionRegistryLite;

import io.grpc.MethodDescriptor;

/**
 * DynamicMessageMarshaller
 *
 * @author thinkerou
 */
public final class DynamicMessageMarshaller implements MethodDescriptor.Marshaller<DynamicMessage> {

    private final Descriptors.Descriptor messageDescriptor;

    /**
     * @param messageDescriptor message descriptor
     */
    public DynamicMessageMarshaller(Descriptors.Descriptor messageDescriptor) {
        this.messageDescriptor = messageDescriptor;
    }

    /**
     * @param inputStream input stream
     * @return DynamicMessage
     */
    @Override
    public DynamicMessage parse(InputStream inputStream) {
        try {
            return DynamicMessage.newBuilder(messageDescriptor)
                    .mergeFrom(inputStream, ExtensionRegistryLite.getEmptyRegistry())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Can't merge from the supplied input stream", e);
        }
    }

    /**
     * @param dynamicMessage dynamic message
     * @return InputStream
     */
    @Override
    public InputStream stream(DynamicMessage dynamicMessage) {
        return dynamicMessage.toByteString().newInput();
    }

}
