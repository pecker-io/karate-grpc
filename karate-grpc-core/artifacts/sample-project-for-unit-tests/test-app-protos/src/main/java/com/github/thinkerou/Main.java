package com.github.thinkerou;

public class Main {

  public static void main(String[] args) {
    int port = Integer.parseInt(System.getProperty("server.port", "8080"));
    LocalGrpcServer server = new LocalGrpcServer(port);
    Runnable task = server::stop;
    Runtime.getRuntime().addShutdownHook(new Thread(task));
    server.start();
  }
}
