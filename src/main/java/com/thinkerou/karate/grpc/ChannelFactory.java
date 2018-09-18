package com.thinkerou.karate.grpc;

import io.grpc.Channel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

/**
 * ChannelFactory
 *
 * @author thinkerou
 */
public class ChannelFactory {

    public Channel create(String host, int port) {
        return NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT).build();
    }

}
