package com.github.thinkerou.karate.utils;

import com.github.fppt.jedismock.RedisServer;

import java.io.IOException;

public class JedisMock {

    private volatile static RedisServer redisServer;

    public static RedisServer getRedisServer() {
        init();
        return redisServer;
    }

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
