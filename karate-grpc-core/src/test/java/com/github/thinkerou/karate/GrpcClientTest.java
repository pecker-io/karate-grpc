package com.github.thinkerou.karate;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.thinkerou.karate.service.Jvm;
import com.github.thinkerou.karate.service.Pair;
import com.github.thinkerou.testng.FudgeClassPath;
import com.github.thinkerou.utils.MiscUtils;
import com.github.thinkerou.utils.ServerSpawner;
import com.google.gson.JsonArray;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.assertj.core.api.AssertionsForClassTypes;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GrpcClientTest {
  private static final int PORT = 8080;
  private ServerSpawner spawner;
  private static final String baseDir = System.getProperty("user.dir") + "/artifacts/";
  private static final String protoBinDir = baseDir + "/protobins";
  private static final String payload = "{\"ticker_symbol\":\"MSFT\",\"company_name\":\"Microsoft Corp\",\"description\":\"Microsoft company\"}";

  private static final Logger log = Logger.getLogger(GrpcClientTest.class.getName());

  @BeforeClass
  public void setup() throws IOException, InterruptedException {
    spawner = new ServerSpawner();
    spawner.start();
    TimeUnit.SECONDS.sleep(5);
    log.info("GRPC Server application has been started");
  }

  @Test
  public void testUnaryMethodWithProtoBinFromJVMArguments() {
    try {
      String location = new File(protoBinDir).getAbsolutePath();
      System.setProperty(Jvm.PROTOBIN_DIR, location);
      runUnaryTest();
    } finally {
      System.setProperty(Jvm.PROTOBIN_DIR, "");
    }
  }

  @Test(dependsOnMethods = "testUnaryMethodWithProtoBinFromJVMArguments")
  @FudgeClassPath
  public void testUnaryMethodWithProtoBinFromResources() {
    runUnaryTest();
  }

  @Test(dependsOnMethods = "testUnaryMethodWithProtoBinFromJVMArguments")
  @FudgeClassPath
  public void testClientStreamingRequests() {
    GrpcClient client = new GrpcClient("localhost", PORT);
    String payload = "["
        + "{\"ticker_symbol\":\"MSFT\",\"company_name\":\"Microsoft Corp\",\"description\":\"Microsoft company\"}"
        + ","
        + "{\"ticker_symbol\":\"AAPL\",\"company_name\":\"Apple Inc\",\"description\":\"Apple company\"}"
        + ","
        + "{\"ticker_symbol\":\"TSLA\",\"company_name\":\"Tesla Inc\",\"description\":\"Tesla company\"}"
        + "]";
    String raw = client.call(
        "com.github.thinkerou.StockQuoteProvider/clientSideStreamingGetStatisticsOfStocks",
        payload);
    String description = MiscUtils.extractDescription(raw);
    AssertionsForClassTypes.assertThat(description)
        .withFailMessage("Should have got a proper response")
        .isEqualTo("Statistics::MSFT:AAPL:TSLA");
  }

  @Test(dependsOnMethods = "testUnaryMethodWithProtoBinFromJVMArguments")
  @FudgeClassPath
  public void testServerStreamingRequests() {
    GrpcClient client = new GrpcClient("localhost", PORT);
    String payload =
        "{\"ticker_symbol\":\"MSFT\",\"company_name\":\"Microsoft Corp\",\"description\":\"Microsoft company\"}";
    String raw = client.call(
        "com.github.thinkerou.StockQuoteProvider/serverSideStreamingGetListStockQuotes",
        payload);
    JsonArray array = MiscUtils.asArray(raw);
    runAssertionsOnArray(array, "Price for stock:MSFT");
  }

  @Test
  @FudgeClassPath
  public void testBidirectionalStreaming() {
    GrpcClient client = new GrpcClient("localhost", PORT);
    String payload = "["
        + "{\"ticker_symbol\":\"AAPL\",\"company_name\":\"Apple Inc\",\"description\":\"Apple company\"}"
        + ","
        + "{\"ticker_symbol\":\"TSLA\",\"company_name\":\"Tesla Inc\",\"description\":\"Tesla company\"}"
        + "]";
    String raw = client.call(
        "com.github.thinkerou.StockQuoteProvider/bidirectionalStreamingGetListsStockQuotes",
        payload);
    JsonArray array = MiscUtils.asArray(raw);
    AssertionsForClassTypes.assertThat(array.size())
        .withFailMessage("We should have got 10 elements in the response")
        .isEqualTo(10);
    JsonArray apple = MiscUtils.filter(array, new Pair<>("description", "Price for stock:AAPL"));
    runAssertionsOnArray(apple, "Price for stock:AAPL");
    JsonArray tesla = MiscUtils.filter(array, new Pair<>("description", "Price for stock:TSLA"));
    runAssertionsOnArray(tesla, "Price for stock:TSLA");
  }

  private static void runAssertionsOnArray(JsonArray array, String descriptionToValidate) {
    final int five = 5;
    AssertionsForClassTypes.assertThat(array.size())
        .withFailMessage("We should have got " + five + " elements in the response")
        .isEqualTo(five);
    AtomicInteger counter = new AtomicInteger(0);
    IntStream.rangeClosed(0, five - 1)
        .mapToObj(array::get)
        .map(each -> each.getAsJsonObject().toString())
        .forEach(each -> {
          AssertionsForClassTypes.assertThat(MiscUtils.extractDescription(each))
              .withFailMessage("We should have got a valid description")
              .isEqualTo(descriptionToValidate);
          AssertionsForClassTypes.assertThat(MiscUtils.extractOfferNumber(each))
              .withFailMessage("We should have got a valid offer number")
              .isEqualTo(counter.incrementAndGet());
        });
  }
  @AfterClass
  public void cleanUp() throws IOException, InterruptedException {
    spawner.stop();
    log.info("GRPC Server application has been stopped");
  }

  private static void runUnaryTest() {
    GrpcClient client = new GrpcClient("localhost", PORT);
    String raw = client.call("com.github.thinkerou.StockQuoteProvider/unaryGetStockQuote", payload);
    String description = MiscUtils.extractDescription(raw);
    assertThat(description)
        .withFailMessage("Should have got a proper response")
        .isEqualTo("Price for stock:MSFT");
  }
}
