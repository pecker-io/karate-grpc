package com.github.thinkerou.karate.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * ChannelFactory
 *
 * @author thinkerou
 */
public class ChannelFactory {

    public static ManagedChannel create(String host, int port) {
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    }

}
