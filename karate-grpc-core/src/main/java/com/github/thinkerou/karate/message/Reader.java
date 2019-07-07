package com.github.thinkerou.karate.message;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

/**
 * Reader
 *
 * A utility class which knows how to read proto files written using message.Writer.
 *
 * @author thinkerou
 */
public final class Reader {

    private static final Logger logger = Logger.getLogger(Reader.class.getName());

    private final JsonFormat.Parser jsonParser;
    private final Descriptors.Descriptor descriptor;
    private final List<Map<String, Object>> payloadList;

    Reader(JsonFormat.Parser jsonParser,
           Descriptors.Descriptor descriptor,
           List<Map<String, Object>> payloadList) {
        this.jsonParser = jsonParser;
        this.descriptor = descriptor;
        this.payloadList = payloadList;
    }

    public static Reader create(Descriptors.Descriptor descriptor,
                                List<Map<String, Object>> payloadList,
                                JsonFormat.TypeRegistry registry) {
        return new Reader(JsonFormat.parser().usingTypeRegistry(registry), descriptor, payloadList);
    }

    public ImmutableList<DynamicMessage> read() {
        ImmutableList.Builder<DynamicMessage> resultBuilder = ImmutableList.builder();

        try {
            payloadList.forEach(value -> {
                DynamicMessage.Builder nextMessage = DynamicMessage.newBuilder(descriptor);
                try {
                    jsonParser.merge(new Gson().toJson(value), nextMessage);
                } catch (InvalidProtocolBufferException e) {
                    logger.warning(e.getMessage());
                }

                resultBuilder.add(nextMessage.build());
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't read messages");
        }

        return resultBuilder.build();
    }

}
