package com.github.thinkerou.karate.utils;

import com.github.fppt.jedismock.RedisServer;

import java.io.IOException;

/**
 * A singleton with a RedisHelper for mock redis (JedisMock)
 *
 * @author ericdriggs
 */
public enum MockRedisHelperSingleton {

    INSTANCE;

    private RedisServer redisServer;
    private RedisHelper redisHelper;
    private volatile boolean isClosed = false;

    MockRedisHelperSingleton() {
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
