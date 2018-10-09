package com.github.thinkerou.demo.helloworld;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

/**
 * HelloWorldServerImpl
 *
 * Source from: https://github.com/grpc/grpc-java/tree/master/examples/src/main/java/io/grpc/examples
 *
 * @author thinkerou
 */
public class HelloWorldServerImpl extends GreeterGrpc.GreeterImplBase {

    private static final Logger logger = Logger.getLogger(HelloWorldServerImpl.class.getName());

    private static final int STREAM_MESSAGE_NUMBER = 10;
    private static final long STREAM_SLEEP_MILLIS = 100;

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void againSayHello(AgainHelloRequest req, StreamObserver<AgainHelloReply> responseObserver) {
        AgainHelloReply reply = AgainHelloReply.newBuilder()
                .setDetails("Details " + req.getMessage() + " in " + req.getAddress())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloServerStreaming(HelloRequest request, StreamObserver<HelloReply> replyStreamObserver) {
        for (int i = 0; i < STREAM_MESSAGE_NUMBER; i++) {
            HelloReply helloReply = HelloReply.newBuilder()
                    .setMessage("Hello " + request.getName() + " part " + i)
                    .build();
            replyStreamObserver.onNext(helloReply);

            try {
                Thread.sleep(STREAM_SLEEP_MILLIS);
            } catch (InterruptedException e) {
                replyStreamObserver.onError(Status.ABORTED.asException());
            }
        }
        replyStreamObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloClientStreaming(final StreamObserver<HelloReply> replyStreamObserver) {
        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest helloRequest) {
                HelloReply helloReply = HelloReply.newBuilder()
                        .setMessage("Hello " + helloRequest.getName())
                        .build();

                try {
                    Thread.sleep(STREAM_SLEEP_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                replyStreamObserver.onNext(helloReply);
                replyStreamObserver.onCompleted();
            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                // Do nothing
            }
        };
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloBiStreaming(final StreamObserver<HelloReply> responseObserver) {
        // Give gRPC a StreamObserver that can observe and process incoming requests.
        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest request) {
                // Process the request and send a response or an error.
                try {
                    // Accept and enqueue the request.
                    String name = request.getName();

                    // Simulate server "work"
                    Thread.sleep(STREAM_SLEEP_MILLIS);

                    // Send a response.
                    String message = "Hello " + name;
                    HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
                    responseObserver.onNext(reply);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    responseObserver.onError(
                            Status.UNKNOWN.withDescription("Error handling request").withCause(throwable).asException());
                }
            }

            @Override
            public void onError(Throwable t) {
                // End the response stream if the client presents an error.
                t.printStackTrace();
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                // Signal the end of work when the client ends the request stream.
                logger.info("COMPLETED");
                responseObserver.onCompleted();
            }
        };
    }

}

