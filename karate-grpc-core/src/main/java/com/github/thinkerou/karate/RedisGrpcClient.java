package com.github.thinkerou.karate;

import com.github.thinkerou.karate.utils.RedisHelper;
import com.intuit.karate.core.ScenarioBridge;

/**
 * RedisGrpcClient
 * A GrpcClient which uses redis to cache protobuf descriptor sets
 *
 * @author ericdriggs
 */
public class RedisGrpcClient extends GrpcClient {

    private final RedisHelper redisHelper;

    /**
     * @param host host
     * @param port port
     * @param redisHelper redis helper
     */
    public RedisGrpcClient(String host, int port, RedisHelper redisHelper) {
        super(host, port);
        this.redisHelper = redisHelper;
    }

    /**
     * @param redisHelper redis helper
     */
    public RedisGrpcClient(RedisHelper redisHelper) {
        super();
        this.redisHelper = redisHelper;
    }

    @Override
    protected String invokeCall(String name, String payload) {
        return callIns.invokeByRedis(name, payload, null, redisHelper);
    }

    @Override
    protected String invokeCall(String name, String payload, ScenarioBridge scenarioBridge) {
        return callIns.invokeByRedis(name, payload, scenarioBridge, redisHelper);
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
