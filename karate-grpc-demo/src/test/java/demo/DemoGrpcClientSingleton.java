package demo;

import com.github.thinkerou.karate.GrpcClient;
import com.github.thinkerou.karate.RedisGrpcClient;
import com.github.thinkerou.karate.utils.MockRedisSingleton;

public enum DemoGrpcClientSingleton {
    INSTANCE;

    RedisGrpcClient redisGrpcClient;

    public GrpcClient getGrpcClient() {
        return redisGrpcClient;
    }

    DemoGrpcClientSingleton() {
        redisGrpcClient = new RedisGrpcClient("localhost", 50051, MockRedisSingleton.INSTANCE.getRedisHelper());
    }
}
