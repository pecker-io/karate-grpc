package com.github.thinkerou.karate;

import com.github.thinkerou.karate.service.GrpcCall;
import com.github.thinkerou.karate.service.GrpcList;
import com.github.thinkerou.karate.utils.RedisHelper;

/**
 * GrpcClient
 *
 * @author thinkerou
 */
public final class GrpcClient {

    private GrpcCall callIns;
    private GrpcList listIns;
    private RedisHelper redisHelper;

    public static GrpcClient create() {
        return new GrpcClient();
    }

    public static GrpcClient create(String host, int port) {
        return new GrpcClient(host, port);
    }

    GrpcClient(String host, int port) {
        this.callIns = GrpcCall.create(host, port);
    }

    GrpcClient() {
        this.listIns = GrpcList.create();
    }

    public GrpcClient redis() {
        if (redisHelper == null) {
            redisHelper = new RedisHelper();
        }
        return this;
    }

    public String call(String name, String payload) {
        if (redisHelper != null) {
            return callIns.invokeByRedis(name, payload, redisHelper);
        }
        return callIns.invoke(name, payload);
    }

    public String list(String serviceFilter, String methodFilter, Boolean withMessage) {
        if (redisHelper != null) {
            return listIns.invokeByRedis(serviceFilter, methodFilter, withMessage, redisHelper);
        }
        return listIns.invoke(serviceFilter, methodFilter, withMessage);
    }

    public String list(String name, Boolean withMessage) {
        if (redisHelper != null) {
            return listIns.invokeByRedis(name, withMessage, redisHelper);
        }
        return listIns.invoke(name, withMessage);
    }

}
