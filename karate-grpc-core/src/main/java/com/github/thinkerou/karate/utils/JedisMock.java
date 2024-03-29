package com.github.thinkerou.karate.utils;

import com.github.fppt.jedismock.RedisServer;

import java.io.IOException;

/**
 * A singleton redis mock using jedis mock
 *
 * @author ericdriggs
 */
public class JedisMock {

    private volatile static RedisServer redisServer;

    /**
     * Returns redis server instance
     * @return redis server
     */
    public static RedisServer getRedisServer() {
        init();
        return redisServer;
    }

    /**
     * Inits redis server instance
     */
    public static void init() {
        if (redisServer != null) {
            return;
        }
        synchronized (JedisMock.class) {
            if (redisServer != null) {
                return;
            }
            try {
                redisServer = RedisServer
                        .newRedisServer()
                        .start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
