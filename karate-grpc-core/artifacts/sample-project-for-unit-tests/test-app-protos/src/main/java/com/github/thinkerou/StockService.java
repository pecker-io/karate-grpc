package com.github.thinkerou;

import com.github.thinkerou.StockQuoteProviderGrpc.StockQuoteProviderImplBase;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class StockService extends StockQuoteProviderImplBase {

  private static final Logger logger = Logger.getLogger(StockService.class.getName());

  @Override
  public void unaryGetStockQuote(Stock request, StreamObserver<StockQuote> responseObserver) {
    StockQuote stock = StockQuote.newBuilder()
        .setPrice(fetchStockPriceBid(request))
        .setOfferNumber(1)
        .setDescription("Price for stock:" + request.getTickerSymbol())
        .build();
    responseObserver.onNext(stock);
    responseObserver.onCompleted();
  }

  @Override
  public void serverSideStreamingGetListStockQuotes(Stock request,
      StreamObserver<StockQuote> responseObserver) {
    IntStream.rangeClosed(1, 5)
        .forEach(i -> {
          StockQuote stockQuote = StockQuote.newBuilder()
              .setPrice(fetchStockPriceBid(request))
              .setOfferNumber(i)
              .setDescription("Price for stock:" + request.getTickerSymbol())
              .build();
          responseObserver.onNext(stockQuote);
        });
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<Stock> clientSideStreamingGetStatisticsOfStocks(
      StreamObserver<StockQuote> responseObserver) {
    return new StreamObserver<Stock>() {
      private int count;
      private double price = 0.0;
      final StringBuffer sb = new StringBuffer();

      @Override
      public void onNext(Stock stock) {
        count++;
        price += fetchStockPriceBid(stock);
        sb.append(":")
            .append(stock.getTickerSymbol());
      }

      @Override
      public void onCompleted() {
        responseObserver.onNext(
            StockQuote.newBuilder()
                .setPrice(price / count)
                .setDescription("Statistics:" + sb)
                .build()
        );
        responseObserver.onCompleted();
      }

      @Override
      public void onError(Throwable t) {
        logger.warning("error: " + t.getMessage());
      }
    };
  }

  @Override
  public StreamObserver<Stock> bidirectionalStreamingGetListsStockQuotes(
      StreamObserver<StockQuote> responseObserver) {
    return new StreamObserver<Stock>() {
      @Override
      public void onNext(Stock request) {
        IntStream.rangeClosed(1, 5)
            .forEach(i -> {
              StockQuote stockQuote = StockQuote.newBuilder()
                  .setPrice(fetchStockPriceBid(request))
                  .setOfferNumber(i)
                  .setDescription("Price for stock:" + request.getTickerSymbol())
                  .build();
              responseObserver.onNext(stockQuote);
            });
      }

      @Override
      public void onError(Throwable t) {
        logger.warning("error:" + t.getMessage());
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
      }
    };
  }

  private static double fetchStockPriceBid(Stock stock) {
    return stock.getTickerSymbol()
        .length()
        + ThreadLocalRandom.current()
        .nextDouble(-0.1d, 0.1d);
  }
}
