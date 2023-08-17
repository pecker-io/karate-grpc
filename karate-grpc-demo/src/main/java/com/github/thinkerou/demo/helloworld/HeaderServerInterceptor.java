package com.github.thinkerou.demo.helloworld;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderServerInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HeaderServerInterceptor.class);

    @VisibleForTesting
    static final Metadata.Key<String> CUSTOM_HEADER_KEY = Metadata.Key.of("karate-test-server-header",
            Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            final Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next) {
        logger.debug("Headers received from client: {}", requestHeaders);
        String clientSpecialHeaderValue = requestHeaders
                .get(Metadata.Key.of("karate-test-header", Metadata.ASCII_STRING_MARSHALLER));
        return next.startCall(new SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendHeaders(Metadata responseHeaders) {
                logger.debug("Found special header value: {}", clientSpecialHeaderValue);
                responseHeaders.put(CUSTOM_HEADER_KEY, clientSpecialHeaderValue);
                super.sendHeaders(responseHeaders);
            }
        }, requestHeaders);
    }
}
