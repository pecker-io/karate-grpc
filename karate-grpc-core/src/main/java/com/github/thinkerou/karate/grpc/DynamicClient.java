package com.github.thinkerou.karate.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.MethodDescriptor.newBuilder;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.github.thinkerou.karate.protobuf.DynamicMessageMarshaller;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
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
public class DynamicClient {

    private final Descriptors.MethodDescriptor protoMethodDescriptor;
    private final Channel channel;

    /**
     * Creates a client for the supplied method, talking to the supplied endpoint.
     */
    public static DynamicClient create(Descriptors.MethodDescriptor protoMethod, Channel channel) {
        return new DynamicClient(protoMethod, channel);
    }

    DynamicClient(Descriptors.MethodDescriptor protoMethodDescriptor, Channel channel) {
        this.protoMethodDescriptor = protoMethodDescriptor;
        this.channel = channel;
    }

    /**
     * Makes an rpc to the remote endpoint and respects the supplied callback. Returns a
     * future which terminates once the call has ended. For calls which are single-request,
     * this throws IllegalArgumentException if the size of requests is not exactly 1.
     */
    public ListenableFuture<Void> call(
            ImmutableList<DynamicMessage> requests,
            StreamObserver<DynamicMessage> responseObsever,
            CallOptions callOptions) {
        Preconditions.checkArgument(!requests.isEmpty(), "Can't make call without any requests");

        long numRequests = requests.size();

        MethodDescriptor.MethodType methodType = getMethodType();
        switch (methodType) {
            case UNARY:
                // Preconditions.checkArgument(numRequests == 1,
                //         "Need exactly 1 request for unary call but got: " + numRequests);
                return callUnary(requests.get(0), responseObsever, callOptions);
            case SERVER_STREAMING:
                Preconditions.checkArgument(numRequests == 1,
                        "Need exactly 1 request for server streaming call but got: " + numRequests);
                return callServerStreaming(requests.get(0), responseObsever, callOptions);
            case CLIENT_STREAMING:
                return callClientStreaming(requests, responseObsever, callOptions);
            case BIDI_STREAMING:
                return callBidiStreaming(requests, responseObsever, callOptions);
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
                CompositeStreamObserver.of(responseObserver, doneObserver));
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
                CompositeStreamObserver.of(responseObserver, doneObserver));
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
                CompositeStreamObserver.of(responseObserver, doneObserver));
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
                CompositeStreamObserver.of(responseObserver, doneObserver));
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
    private MethodDescriptor.MethodType getMethodType() {
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
