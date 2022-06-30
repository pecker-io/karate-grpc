package com.github.thinkerou.karate.utils;

import com.github.fppt.jedismock.RedisServer;
import com.github.thinkerou.karate.constants.RedisParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * RedisHelper
 *
 * @author thinkerou
 */
public final class RedisHelper {

    private static final int REDIS_TIMEOUT = 10000;

    private static volatile JedisPool jedisPool;

    public static Jedis getJedis() {
        init();
        return jedisPool.getResource();
    }

    public Boolean putDescriptorSets(Path descriptorPath) {
        byte[] data;
        try {
            data = Files.readAllBytes(descriptorPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (Jedis jedis = getJedis()){
            Long status = jedis.hset(RedisParams.KEY.getText(), RedisParams.FIELD.getText(), data);
            if (status != 1) {
                return false;
            }
            return true;
        }
    }

    public byte[] getDescriptorSets() {
        try ( Jedis jedis = getJedis()) {
            return jedis.hget(RedisParams.KEY.getText(), RedisParams.FIELD.getText());
        }
    }

    public Long deleteDescriptorSets() {
        try (Jedis jedis = getJedis()){
            return jedis.hdel(RedisParams.KEY.getText(), RedisParams.FIELD.getText());
        }
    }

    public static synchronized void closeJedisPool() {
        if (jedisPool != null && jedisPool.isClosed()) {
            jedisPool.close();
        }
    }
    private static void init() {
        if (jedisPool != null) {
            return;
        }
        synchronized (RedisHelper.class) {
            if (jedisPool != null) {
                return;
            }
            RedisServer redisServer = JedisMock.getRedisServer();
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(128);
            jedisPool = new JedisPool(poolConfig, redisServer.getHost(), redisServer.getBindPort(), REDIS_TIMEOUT);
        }
    }
}
