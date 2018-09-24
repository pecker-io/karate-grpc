package com.github.thinkerou.karate.message;

import java.io.ByteArrayOutputStream;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import io.grpc.stub.StreamObserver;

/**
 * Writer
 *
 * A StreamObserver which writes the contents of the received messages to an Output.
 * The messages are writting in a newline-separated json format.
 *
 * @author thinkerou
 */
public class Writer<T extends Message> implements StreamObserver<T> {

    // Used to separate the individual plaintext json proto messages.
    private static final String MESSAGE_SEPARATOR = "\n\n";

    private final JsonFormat.Printer jsonPrinter;
    private final Output output;

    /**
     * Creates a new Writer which writes the messages it sees to the supplied Output.
     */
    public static <T extends Message> Writer<T> create(Output output, JsonFormat.TypeRegistry registry) {
        return new Writer<>(JsonFormat.printer().usingTypeRegistry(registry), output);
    }

    /**
     * Returns the string representation of the stream of supplied messages. Each individual message
     * is represented as valid json, but not that the whole result is, itself, *not* valid json.
     */
    public static <M extends Message> String writeJsonStream(ImmutableList<M> messages) {
        return writeJsonStream(messages, JsonFormat.TypeRegistry.getEmptyTypeRegistry());
    }

    public static <M extends Message> String writeJsonStream(
            ImmutableList<M> messages, JsonFormat.TypeRegistry registry) {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        Writer<M> writer = Writer.create(null, registry);
        writer.writeAll(messages);

        return resultStream.toString();
    }

    Writer(JsonFormat.Printer jsonPrinter, Output output) {
        this.jsonPrinter = jsonPrinter;
        this.output = output;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onNext(T message) {
        try {
            output.write(jsonPrinter.print(message) + MESSAGE_SEPARATOR);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes all the supplied messages and closes the stream.
     */
    public void writeAll(ImmutableList<? extends T> messages) {
        messages.forEach(this::onNext);
        onCompleted();
    }

}
