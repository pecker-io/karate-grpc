package com.github.thinkerou.karate;

import com.github.thinkerou.karate.utils.RedisHelper;

/**
 * RedisGrpcClient
 * A GrpcClient which uses redis to cache protobuf descriptor sets
 *
 * @author ericdriggs
 */
public class RedisGrpcClient extends GrpcClient {

    private RedisHelper redisHelper;

    public RedisGrpcClient(String host, int port, RedisHelper redisHelper) {
        super(host, port);
        this.redisHelper = redisHelper;
    }

    public RedisGrpcClient(RedisHelper redisHelper) {
        super();
        this.redisHelper = redisHelper;
    }

    @Override
    protected String invokeCall(String name, String payload) {
        return callIns.invokeByRedis(name, payload, redisHelper);
    }

    @Override
    protected String invokeList(String serviceFilter, String methodFilter, Boolean withMessage) {
        return listIns.invokeByRedis(serviceFilter, methodFilter, withMessage, redisHelper);
    }

    @Override
    protected String invokeList(String name, Boolean withMessage) {
        return listIns.invokeByRedis(name, withMessage, redisHelper);
    }

}
