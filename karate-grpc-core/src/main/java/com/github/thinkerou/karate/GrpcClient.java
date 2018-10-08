package com.github.thinkerou.karate;

import java.io.IOException;

import com.github.thinkerou.karate.service.GrpcCall;
import com.github.thinkerou.karate.service.GrpcList;

/**
 * GrpcClient
 *
 * @author thinkerou
 */
public class GrpcClient {

    private static GrpcCall callIns;
    private static GrpcList listIns;

    public static GrpcClient create() {
        listIns = GrpcList.create();
        return new GrpcClient();
    }

    public static GrpcClient create(String host, int port) {
        callIns = GrpcCall.create(host, port);
        return new GrpcClient();
    }

    public String call(String name, String payload) throws IOException {
        return callIns.invoke(name, payload);
    }

    public String list(String serviceFilter, String methodFilter, Boolean withMessage) throws IOException {
        return listIns.invoke(serviceFilter, methodFilter, withMessage);
    }

    public String list(String name, Boolean withMessage) throws IOException {
        return listIns.invoke(name, withMessage);
    }

}
