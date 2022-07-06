package com.github.thinkerou.karate.utils;

import com.github.fppt.jedismock.RedisServer;

import java.io.IOException;

/**
 * RedisHelper
 *
 * @author thinkerou
 */
public enum MockRedisSingleton  {

    INSTANCE;

    private RedisServer redisServer;
    private RedisHelper redisHelper;
    private volatile boolean isClosed = false;

    MockRedisSingleton() {
        redisServer = JedisMock.getRedisServer();
        redisHelper = new RedisHelper(redisServer.getHost(), redisServer.getBindPort(), 10000, 128);
    }

    public RedisServer getRedisServer() {
        return redisServer;
    }

    public RedisHelper getRedisHelper() {
        return redisHelper;
    }

    public synchronized void stop() throws IOException {
        if (isClosed) {
            return;
        }
        redisHelper.closeJedisPool();
        redisServer.stop();
        isClosed = true;
    }
}
