package com.github.thinkerou.karate.grpc;

import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;

import io.grpc.stub.StreamObserver;

/**
 * ComponentObserver
 *
 * A StreamObserver which groups multiple observers and executes them all.
 *
 * @author thinkerou
 */
public class ComponentObserver<T> implements StreamObserver<T> {

    private static final Logger logger = Logger.getLogger(ComponentObserver.class.getName());

    private final ImmutableList<StreamObserver<T>> observers;

    @SafeVarargs
    public static <T> ComponentObserver<T> of(StreamObserver<T>... observers) {
        return new ComponentObserver<>(ImmutableList.copyOf(observers));
    }

    private ComponentObserver(ImmutableList<StreamObserver<T>> observers) {
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
