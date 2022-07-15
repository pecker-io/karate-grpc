package com.github.thinkerou.karate.utils;

import com.github.fppt.jedismock.RedisServer;

import java.io.IOException;

/**
 * A singleton with a RedisHelper for mock redis (JedisMock)
 *
 * @author ericdriggs
 */
public enum MockRedisHelperSingleton {

    INSTANCE,
    ;

    private final RedisServer redisServer;
    private final RedisHelper redisHelper;
    private volatile boolean isStopped = false;

    MockRedisHelperSingleton() {
        redisServer = JedisMock.getRedisServer();
        redisHelper = new RedisHelper(redisServer.getHost(), redisServer.getBindPort(), 10000, 128);
    }

    /**
     * @return redis server
     */
    public RedisServer getRedisServer() {
        return redisServer;
    }

    /**
     * @return redis helper
     */
    public RedisHelper getRedisHelper() {
        return redisHelper;
    }

    /**
     * @throws IOException io exception
     */
    public synchronized void stop() throws IOException {
        if (isStopped) {
            return;
        }
        redisHelper.closeJedisPool();
        redisServer.stop();
        isStopped = true;
    }

}
