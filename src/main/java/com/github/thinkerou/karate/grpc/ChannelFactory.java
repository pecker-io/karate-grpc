package com.github.thinkerou.karate.grpc;

import io.grpc.Channel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

/**
 * ChannelFactory
 *
 * @author thinkerou
 */
public class ChannelFactory {

    public static Channel create(String host, int port) {
        return NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT).build();
    }

}
