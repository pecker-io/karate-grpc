package com.github.thinkerou.karate;

import com.github.thinkerou.karate.service.GrpcCall;
import com.github.thinkerou.karate.service.GrpcList;

/**
 * GrpcClient
 *
 * @author thinkerou
 */
public class GrpcClient {

    protected GrpcCall callIns;
    protected GrpcList listIns;

    public GrpcClient(String host, int port) {
        this.callIns = GrpcCall.create(host, port);
    }

    public GrpcClient() {
        this.listIns = GrpcList.create();
    }

    public String call(String name, String payload) {
        return callIns.invoke(name, payload);
    }

    public String list(String serviceFilter, String methodFilter, Boolean withMessage) {
        return listIns.invoke(serviceFilter, methodFilter, withMessage);
    }

    public String list(String name, Boolean withMessage) {
        return listIns.invoke(name, withMessage);
    }

}
