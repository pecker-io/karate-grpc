package com.github.thinkerou.karate.constants;

/**
 * RedisParams
 *
 * @author thinkerou
 */
public enum RedisParams {

    KEY("karate-grpc-protobuf".getBytes()),
    FIELD("file-descriptor-sets".getBytes()),
    ;

    private final byte[] text;

    /**
     * @param text text
     */
    RedisParams(byte[] text) {
        this.text = text;
    }

    /**
     * @return byte[]
     */
    public byte[] getText() {
        return text;
    }

}
