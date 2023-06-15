package com.github.thinkerou.karate.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.MethodDescriptor.newBuilder;

import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.github.thinkerou.karate.protobuf.DynamicMessageMarshaller;

import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

/**
 * DynamicClient
 *
 * A grpc client which operates on dynamic messages.
 *
 * @author thinkerou
 */
public final class DynamicClient {

    private static final Logger logger = Logger.getLogger(DynamicClient.class.getName());

    private final Descriptors.MethodDescriptor protoMethodDescriptor;
    private final ManagedChannel channel;

    /**
     * Creates a client for the supplied method, talking to the supplied endpoint.
     * @param protoMethod protoMethod
     * @param channel channel
     * @return DynamicClient
     */
    public static DynamicClient create(Descriptors.MethodDescriptor protoMethod, ManagedChannel channel) {
        return new DynamicClient(protoMethod, channel);
    }

    /**
     * @param protoMethodDescriptor proto method descriptor
     * @param channel channel
     */
    DynamicClient(Descriptors.MethodDescriptor protoMethodDescriptor, ManagedChannel channel) {
        this.protoMethodDescriptor = protoMethodDescriptor;
        this.channel = channel;
    }

    /**
     * Makes an rpc to the remote endpoint and respects the supplied callback. Returns a
     * future which terminates once the call has ended. For calls which are single-request,
     * this throws IllegalArgumentException if the size of requests is not exactly 1.
     *
     * @param requests requests
     * @param responseObserver responseObsever
     * @param callOptions callOptions
     * @return ListenableFuture
     */
    public ListenableFuture<Void> call(
            ImmutableList<DynamicMessage> requests,
            StreamObserver<DynamicMessage> responseObserver,
            CallOptions callOptions) {
        if (requests.isEmpty()) {
            logger.warning("Can't make call without any requests");
            return null;
        }

        long numRequests = requests.size();

        MethodDescriptor.MethodType methodType = getMethodType();
        switch (methodType) {
            case UNARY:
                if (numRequests != 1) {
                    logger.warning("Need exactly 1 request for unary call but got: " + numRequests);
                }
                return callUnary(requests.get(0), responseObserver, callOptions);
            case SERVER_STREAMING:
                if (numRequests != 1) {
                    logger.warning("Need exactly 1 request for server streaming call but got: " + numRequests);
                }
                return callServerStreaming(requests.get(0), responseObserver, callOptions);
            case CLIENT_STREAMING:
                logger.warning("Client stream call");
                return callClientStreaming(requests, responseObserver, callOptions);
            case BIDI_STREAMING:
                return callBidiStreaming(requests, responseObserver, callOptions);
            case UNKNOWN:
                return null;
        }
        return null;
    }

    private ListenableFuture<Void> callBidiStreaming(
            ImmutableList<DynamicMessage> requests,
            StreamObserver<DynamicMessage> responseObserver,
            CallOptions callOptions) {
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        StreamObserver<DynamicMessage> requestObserver = ClientCalls.asyncBidiStreamingCall(
                createCall(callOptions),
                ComponentObserver.of(responseObserver, doneObserver));
        requests.forEach(requestObserver::onNext);
        requestObserver.onCompleted();
        return doneObserver.getCompletionFuture();
    }

    private ListenableFuture<Void> callClientStreaming(
            ImmutableList<DynamicMessage> requests,
            StreamObserver<DynamicMessage> responseObserver,
            CallOptions callOptions) {
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        StreamObserver<DynamicMessage> requestObserver = ClientCalls.asyncClientStreamingCall(
                createCall(callOptions),
                ComponentObserver.of(responseObserver, doneObserver));
        requests.forEach(requestObserver::onNext);
        requestObserver.onCompleted();
        return doneObserver.getCompletionFuture();
    }

    private ListenableFuture<Void> callServerStreaming(
            DynamicMessage request,
            StreamObserver<DynamicMessage> responseObserver,
            CallOptions callOptions) {
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        ClientCalls.asyncServerStreamingCall(
                createCall(callOptions),
                request,
                ComponentObserver.of(responseObserver, doneObserver));
        return doneObserver.getCompletionFuture();
    }

    private ListenableFuture<Void> callUnary(
            DynamicMessage request,
            StreamObserver<DynamicMessage> responseObserver,
            CallOptions callOptions) {
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        ClientCalls.asyncUnaryCall(
                createCall(callOptions),
                request,
                ComponentObserver.of(responseObserver, doneObserver));
        return doneObserver.getCompletionFuture();
    }

    private ClientCall<DynamicMessage, DynamicMessage> createCall(CallOptions callOptions) {
        return channel.newCall(createGrpcMethodDescriptor(), callOptions);
    }

    private MethodDescriptor<DynamicMessage, DynamicMessage> createGrpcMethodDescriptor() {
        MethodDescriptor.Builder builder = newBuilder();

        builder.setType(getMethodType())
                .setFullMethodName(getFullMethodName())
                .setRequestMarshaller(new DynamicMessageMarshaller(protoMethodDescriptor.getInputType()))
                .setResponseMarshaller(new DynamicMessageMarshaller(protoMethodDescriptor.getOutputType()));

        return builder.build();
    }

    private String getFullMethodName() {
        String serviceName = protoMethodDescriptor.getService().getFullName();
        String methodName = protoMethodDescriptor.getName();

        return generateFullMethodName(serviceName, methodName);
    }

    /**
     * Returns the appropriate method type based on whether the client or server expect streams.
     */
    public MethodDescriptor.MethodType getMethodType() {
        boolean clientStreaming = protoMethodDescriptor.toProto().getClientStreaming();
        boolean serverStreaming = protoMethodDescriptor.toProto().getServerStreaming();

        if (!clientStreaming && !serverStreaming) {
            return MethodDescriptor.MethodType.UNARY;
        } else if (!clientStreaming && serverStreaming) {
            return MethodDescriptor.MethodType.SERVER_STREAMING;
        } else if (clientStreaming && !serverStreaming) {
            return MethodDescriptor.MethodType.CLIENT_STREAMING;
        } else {
            return MethodDescriptor.MethodType.BIDI_STREAMING;
        }
    }

}
