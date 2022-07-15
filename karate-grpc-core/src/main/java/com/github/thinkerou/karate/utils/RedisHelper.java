package com.github.thinkerou.karate.utils;

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
public class RedisHelper {

    protected JedisPool jedisPool;
    protected String host;
    protected int port;
    protected int timeout;

    /**
     * @param host host
     * @param port port
     * @param timeout timeout
     * @param maxConnections max connections
     */
    public RedisHelper(String host, int port, int timeout, int maxConnections) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxConnections);
        jedisPool = new JedisPool(poolConfig, host, port, timeout);
    }

    /**
     * @return jedis
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void closeJedisPool() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    /**
     * @param descriptorPath description path
     * @return boolean
     */
    public Boolean putDescriptorSets(Path descriptorPath) {
        byte[] data;
        try {
            data = Files.readAllBytes(descriptorPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (Jedis jedis = getJedis()) {
            long status = jedis.hset(RedisParams.KEY.getText(), RedisParams.FIELD.getText(), data);
            return status == 1;
        }
    }

    /**
     * @return byte[]
     */
    public byte[] getDescriptorSets() {
        try (Jedis jedis = getJedis()) {
            return jedis.hget(RedisParams.KEY.getText(), RedisParams.FIELD.getText());
        }
    }

    /**
     * @return long
     */
    public Long deleteDescriptorSets() {
        try (Jedis jedis = getJedis()) {
            return jedis.hdel(RedisParams.KEY.getText(), RedisParams.FIELD.getText());
        }
    }
}
