package demo;

import com.github.thinkerou.karate.GrpcClient;
import com.github.thinkerou.karate.RedisGrpcClient;
import com.github.thinkerou.karate.utils.MockRedisHelperSingleton;

/**
 * @author thinkerou
 */
public enum DemoGrpcClientSingleton {

    INSTANCE,
    ;

    RedisGrpcClient redisGrpcClient;

    /**
     * @return grpc client
     */
    public GrpcClient getGrpcClient() {
        return redisGrpcClient;
    }

    DemoGrpcClientSingleton() {
        redisGrpcClient = new RedisGrpcClient("localhost", 50051,
                                              MockRedisHelperSingleton.INSTANCE.getRedisHelper());
    }

}
