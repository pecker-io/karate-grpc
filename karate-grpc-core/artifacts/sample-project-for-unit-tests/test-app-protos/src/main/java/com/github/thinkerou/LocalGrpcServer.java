package com.github.thinkerou;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

//Sample format for interaction
//grpcurl --plaintext -d '{"ticker_symbol":"MSFT","company_name":"Microsoft Corp","description":"Microsoft company"}' \
//localhost:8080 com.github.thinkerou.StockQuoteProvider/unaryGetStockQuote

public class LocalGrpcServer {

  private final Server server;

  public LocalGrpcServer(int port) {
    server = ServerBuilder.forPort(port)
        .addService(new StockService())
        .addService(ProtoReflectionService.newInstance())
        .build();
  }

  public void start() {
    try {
      server.start();
      server.awaitTermination();
    }catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    try {
      server.shutdownNow().awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
