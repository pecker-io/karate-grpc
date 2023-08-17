package com.github.thinkerou.karate.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class GrpcClientRequestInterceptor implements ClientInterceptor {

    private final Metadata requestMetadata;
    private Metadata responseMetadata;

    public Metadata getResponseMetadata() {
        return responseMetadata;
    }

    public GrpcClientRequestInterceptor(Metadata metadata) {
        this.requestMetadata = metadata;
    }

    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            final MethodDescriptor<ReqT, RespT> methodDescriptor,
            final CallOptions callOptions,
            final Channel channel) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                channel.newCall(methodDescriptor, callOptions)) {

            @Override
            public void start(ClientCall.Listener<RespT> responseListener, Metadata headers) {
                headers = requestMetadata;
                super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        responseMetadata = headers;
                        super.onHeaders(headers);
                    }
                }, headers);

            }
        };
    }
}
