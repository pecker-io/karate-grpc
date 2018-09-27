package com.github.thinkerou.karate.grpc;

import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;

import io.grpc.stub.StreamObserver;

/**
 * CompositeStreamObserver
 *
 * A StreamObserver which groups multiple observers and executes them all.
 *
 * @author thinkerou
 */
public class CompositeStreamObserver<T> implements StreamObserver<T> {

    private static final Logger logger = Logger.getLogger(CompositeStreamObserver.class.getName());

    private final ImmutableList<StreamObserver<T>> observers;

    @SafeVarargs
    public static <T> CompositeStreamObserver<T> of(StreamObserver<T>... observers) {
        return new CompositeStreamObserver<>(ImmutableList.copyOf(observers));
    }

    private CompositeStreamObserver(ImmutableList<StreamObserver<T>> observers) {
        this.observers = observers;
    }

    @Override
    public void onCompleted() {
        observers.forEach(tStreamObserver -> {
            try {
                tStreamObserver.onCompleted();
            } catch (Throwable t) {
                logger.warning(t.getMessage());
            }
        });
    }

    @Override
    public void onError(Throwable t) {
        observers.forEach(tStreamObserver -> {
            try {
                tStreamObserver.onError(t);
            } catch (Throwable e) {
                logger.warning(e.getMessage());
            }
        });
    }

    @Override
    public void onNext(T value) {
        observers.forEach(tStreamObserver -> {
            try {
                tStreamObserver.onNext(value);
            } catch (Throwable t) {
                logger.warning(t.getMessage());
            }
        });
    }

}
