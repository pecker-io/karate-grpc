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

    private byte[] text;

    RedisParams(byte[] text) {
        this.text = text;
    }

    public byte[] getText() {
        return text;
    }

}
