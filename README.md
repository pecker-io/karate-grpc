# karate-grpc

[![Build Status](https://api.travis-ci.org/thinkerou/karate-grpc.svg)](https://travis-ci.org/thinkerou/karate-grpc)

karate-grpc made gRPC testing simple by [karate](https://github.com/intuit/karate), and its dynamic client built based on [polyglot](https://github.com/grpc-ecosystem/polyglot).

karate-grpc can get all the benefits of [karate](https://github.com/intuit/karate#features), it makes it really easy to build protobuf complex request payloads via json, traverse data within the responses and chain data from responses into the next request.

## Hello World
<a href="https://github.com/thinkerou/karate-grpc/blob/master/karate-grpc-demo/src/test/java/demo/helloworld/helloworld-new.feature"><img src="assets/karate-grpc-hello-world.png" /></a>

## Testing hello world

Prefer to use Maven:

```
$ # compile the whole project
$ mvn clean compile package -Dmaven.test.skip=true
$ cd karate-grpc-demo
$ # run all tests
$ mvn test
$ # or run single test
$ mvn test -Dtest=HelloWorldNewRunner
```

Because have started hello world server on `TestBase.java`, we not need to start it alone.

## Getting Started

> karate-grpc only support Maven currently.

You need to add the following `<dependencies>`:

```maven
<dependency>
    <groupId>com.github.thinkerou</groupId>
    <artifactId>karate-grpc-core</artifactId>
    <version>0.4.11</version>
</dependency>
```

## What to need for testing grpc server

Testing one grpc server, we have the follow info:

- grpc server `ip` and `port`

- protobuf file corresponding grpc server, but usually it's a protobuf jar package not one single file.

So, we should test it based on the two point.

Using Karate we can perfect to solve it!

## How to write grpc client

**Note:**

> The part content is outdated draft which initially think about the topic which continues to have saved is for reference only.

> Usually you no need to care it and skip it, because `karate-grpc-core` have completed the function.

You only need two steps:

- Read json file and parse protobuf object to the request of grpc server

- format the response of grpc server to json string and return it as grpc server

Like this:

```java
public class Client {

  // ...

  public static String greet(String input) {
    HelloRequest.Builder requestBuilder = HelloRequest.newBuilder();
    try {
      JsonFormat.parser().merge(input, requestBuilder);
    } catch (ProtocolBufferException e) {
      // ...
    }

    HelloReply response = null;
    try {
      response = blockingStub.sayHello(requestBuilder.build());
    } catch (StatusRuntimeException e) {
      // ...
    }

    String res = "";
    try {
      res = JsonFormat.printer().print(response);
    } catch (ProtocolBufferException e) {
      // ...
    }

    return res;
  }

  // ...
}

```

## How to write karate feature

We need to use [Java interop](https://github.com/intuit/karate#java-interop) of Karate in order to call us define grpc client.

And use `JSON.parse` javascript function parse the response of grpc server return value.

Like this:

```
Feature: grpc hello world example

  Background:
    * def Client = Java.type('HelloWorldClient')
    * def config = { host: 'localhost', port: 50051, extra: 'other config information' }
    * def client = new Client(config.host, config.port)

  Scenario: do it
    * def payload = read('helloworld.json')
    * def response = client.greet(payload)
    * eval client.shutdown()
    * def response = JSON.parse(response)
    * match response.message == 'Hello thinkerou'
```

That's all!

## Thanks 

Thanks [Peter Thomas](https://github.com/ptrthomas) for his work for [karate](https://github.com/intuit/karate) and his generous help, also thanks [Dino Wernli](https://github.com/dinowernli) for his contributions for [polyglot](https://github.com/grpc-ecosystem/polyglot).

## Reference

Maybe you want to know more information about Karate or other, please read the follow contents:

- karate project home: [https://github.com/intuit/karate](https://github.com/intuit/karate)

- polyglot project home: [https://github.com/grpc-ecosystem/polyglot](https://github.com/grpc-ecosystem/polyglot)

- ProtoBuf(via JSON) project home: [https://github.com/protocolbuffers/protobuf/tree/master/java](https://github.com/protocolbuffers/protobuf/tree/master/java)

- grpc-java project home: [https://github.com/grpc/grpc-java](https://github.com/grpc/grpc-java)

## License

[karate-grpc](https://thinkerou.com/karate-grpc/) is licensed under MIT License.
