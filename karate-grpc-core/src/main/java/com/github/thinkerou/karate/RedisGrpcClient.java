package com.github.thinkerou.karate;

import com.github.thinkerou.karate.service.GrpcCall;
import com.github.thinkerou.karate.service.GrpcList;
import com.github.thinkerou.karate.utils.RedisHelper;

/**
 * GrpcClient
 *
 * @author thinkerou
 */
public class RedisGrpcClient extends GrpcClient {

    private GrpcCall callIns;
    private GrpcList listIns;
    private RedisHelper redisHelper;


    public RedisGrpcClient(String host, int port, RedisHelper redisHelper) {
        this.callIns = GrpcCall.create(host, port);
        this.redisHelper = redisHelper;
    }

    public RedisGrpcClient(RedisHelper redisHelper) {
        this.listIns = GrpcList.create();
        this.redisHelper = redisHelper;
    }

    public String call(String name, String payload) {
        return callIns.invokeByRedis(name, payload, redisHelper);

    }

    public String list(String serviceFilter, String methodFilter, Boolean withMessage) {

        return listIns.invokeByRedis(serviceFilter, methodFilter, withMessage, redisHelper);

    }

    public String list(String name, Boolean withMessage) {
        return listIns.invokeByRedis(name, withMessage, redisHelper);
    }

}
