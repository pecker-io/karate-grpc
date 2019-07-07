package com.github.thinkerou.karate.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.thinkerou.karate.constants.RedisParams;

import redis.clients.jedis.Jedis;

/**
 * RedisHelper
 *
 * @author thinkerou
 */
public final class RedisHelper {

    private static final int REDIS_TIMEOUT = 3000;

    private static Jedis jedis;

    public static RedisHelper create(String host, int port) {
        return new RedisHelper(host, port);
    }

    RedisHelper(String host, int port) {
        jedis = new Jedis(host, port, REDIS_TIMEOUT);
    }

    public Boolean putDescriptorSets(Path descriptorPath) {
        byte[] data;
        try {
            data = Files.readAllBytes(descriptorPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Long status = jedis.hset(RedisParams.KEY.getText(), RedisParams.FIELD.getText(), data);
        if (status != 1) {
            return false;
        }

        return true;
    }

    public byte[] getDescriptorSets() {
        return jedis.hget(RedisParams.KEY.getText(), RedisParams.FIELD.getText());
    }

    public Long deleteDescriptorSets() {
        return jedis.hdel(RedisParams.KEY.getText(), RedisParams.FIELD.getText());
    }
}
