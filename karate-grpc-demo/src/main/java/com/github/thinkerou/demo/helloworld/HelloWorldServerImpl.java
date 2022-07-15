package com.github.thinkerou.demo.helloworld;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    private static final int STREAM_MESSAGE_NUMBER = 3;
    private static final long STREAM_SLEEP_MILLIS = 10;

    private static final double COORD_FACTOR = 1e7;

    private final Collection<Feature> features;
    private final ConcurrentMap<Point, List<RouteNote>> routeNotes = new ConcurrentHashMap<Point, List<RouteNote>>();

    /**
     * @param features
     */
    HelloWorldServerImpl(Collection<Feature> features) {
        this.features = features;
    }

    /**
     * @param req
     * @param responseObserver
     */
    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * @param req
     * @param responseObserver
     */
    @Override
    public void againSayHello(AgainHelloRequest req, StreamObserver<AgainHelloReply> responseObserver) {
        AgainHelloReply reply = AgainHelloReply.newBuilder()
                .setDetails("Details " + req.getMessage() + " in " + req.getAddress())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * @param request
     * @param replyStreamObserver
     */
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

    /**
     * @param replyStreamObserver
     * @return
     */
    @Override
    public StreamObserver<HelloRequest> sayHelloClientStreaming(final StreamObserver<HelloReply> replyStreamObserver) {
        return new StreamObserver<HelloRequest>() {
            String out = "";

            @Override
            public void onNext(HelloRequest helloRequest) {
                if (out == "") {
                    out = helloRequest.getName();
                } else {
                    out += " and " + helloRequest.getName();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                HelloReply helloReply = HelloReply.newBuilder()
                        .setMessage("Hello " + out)
                        .build();
                replyStreamObserver.onNext(helloReply);
                replyStreamObserver.onCompleted();
            }
        };
    }

    /**
     * @param responseObserver
     * @return
     */
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
                responseObserver.onCompleted();
            }
        };
    }

    // The following part comes from:
    // https://github.com/grpc/grpc-java/tree/master/examples/src/main/java/io/grpc/examples/routeguide

    /**
     * getFeature: single grpc
     *
     * Gets the Feature at the requested Point.
     * If no feature at that location exists, an unnamed feature is returned at the provided location.
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void getFeature(Point request, StreamObserver<Feature> responseObserver) {
        responseObserver.onNext(checkFeature(request));
        responseObserver.onCompleted();
    }

    /**
     * listFeaturs: server stream grpc
     *
     * Gets all features contained within the given bounding Rectangle.
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void listFeatures(Rectangle request, StreamObserver<Feature> responseObserver) {
        int left = min(request.getLo().getLongitude(), request.getHi().getLongitude());
        int right = max(request.getLo().getLongitude(), request.getHi().getLongitude());
        int top = max(request.getLo().getLatitude(), request.getHi().getLatitude());
        int bottom = min(request.getLo().getLatitude(), request.getHi().getLatitude());

        for (Feature feature : features) {
            if (!exists(feature)) {
                continue;
            }

            int lat = feature.getLocation().getLatitude();
            int lon = feature.getLocation().getLongitude();
            if (lon >= left && lon <= right && lat >= bottom && lat <= top) {
                responseObserver.onNext(feature);
            }
        }

        responseObserver.onCompleted();
    }

    /**
     * recordRoute: client stream grpc
     *
     * Gets a stream of points, and responds with statistics about the "trip": number of points,
     * number of known features visited, total distance traveled, and total time spent.
     *
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<Point> recordRoute(final StreamObserver<RouteSummary> responseObserver) {
        return new StreamObserver<Point>() {
            int pointCount;
            int featureCount;
            int distance;
            Point previous;
            final  long startTime = System.nanoTime();

            @Override
            public void onNext(Point point) {
                pointCount++;
                if (exists(checkFeature(point))) {
                    featureCount++;
                }
                if (previous != null) {
                    distance += calcDistance(previous, point);
                }
                previous = point;
            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning("RecordRoute cancelled");
            }

            @Override
            public void onCompleted() {
                long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
                responseObserver.onNext(RouteSummary.newBuilder().
                        setPointCount(pointCount)
                        .setFeatureCount(featureCount)
                        .setDistance(distance)
                        .setElapsedTime((int) seconds)
                        .build()
                );
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * routeChat: bidi stream grpc
     *
     * Receives a stream of message/location pairs, and responds with a stream of all previous
     * messages at each of those locations.
     *
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<RouteNote> routeChat(final StreamObserver<RouteNote> responseObserver) {
        return new StreamObserver<RouteNote>() {
            @Override
            public void onNext(RouteNote routeNote) {
                List<RouteNote> notes = getOrCreateNotes(routeNote);

                for (RouteNote preNote : notes.toArray(new RouteNote[0])) {
                    responseObserver.onNext(preNote);
                }

                notes.add(routeNote);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning("RouteChat cancelled");
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * Gets the notes list for the given location. If missing, create it.
     */
    private List<RouteNote> getOrCreateNotes(RouteNote routeNote) {
        List<RouteNote> notes = Collections.synchronizedList(new ArrayList<>());
        notes.add(routeNote);

        List<RouteNote> preNotes = routeNotes.putIfAbsent(routeNote.getLocation(), notes);

        return preNotes != null ? preNotes : notes;
    }

    /**
     * Gets the feature at the given point.
     */
    private Feature checkFeature(Point location) {
        for (Feature feature : features) {
            if (feature.getLocation().getLongitude() == location.getLongitude() &&
                    feature.getLocation().getLatitude() == location.getLatitude()) {
                return feature;
            }
        }

        return Feature.newBuilder().setName("").setLocation(location).build();
    }

    /**
     * Calculates the distance between two points using the "haversine" formula.
     */
    private static int calcDistance(Point start, Point end) {
        int r = 6371000; // earch radius in meters
        double lat1 = toRadians(start.getLatitude() / COORD_FACTOR);
        double lat2 = toRadians(end.getLatitude() / COORD_FACTOR);
        double lon1 = toRadians(start.getLongitude() / COORD_FACTOR);
        double lon2 = toRadians(end.getLongitude() / COORD_FACTOR);

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = sin(deltaLat / 2) * sin(deltaLat / 2) + cos(lat1) * cos(lat2) * sin(deltaLon / 2) * sin(deltaLon / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));

        return (int) (r * c);
    }

    /**
     * Indicates whether the given feature exists.
     *
     * @param feature
     */
    public static boolean exists(Feature feature) {
        return feature != null && !feature.getName().isEmpty();
    }

}

