package com.github.thinkerou.karate.message;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
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
public class Reader {

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
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(value.toString());

                DynamicMessage.Builder nextMessage = DynamicMessage.newBuilder(descriptor);
                try {
                    jsonParser.merge(stringBuilder.toString(), nextMessage);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

                resultBuilder.add(nextMessage.build());
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't read messages");
        }

        return resultBuilder.build();
    }

}
