# karate-grpc

[![Build Status](https://api.travis-ci.org/thinkerou/karate-grpc.svg)](https://travis-ci.org/thinkerou/karate-grpc)

gRPC Testing Made Simple by [Karate](https://github.com/intuit/karate).

## testing helloworld

Prefer to use Maven:

```
$ mvn verify
$ # Run the server
$ mvn exec:java -Dexec.mainClass=example.helloworld.HelloWorldServer
$ # In another terminal run the client
$ mvn exec:java -Dexec.mainClass=example.helloworld.HelloWorldClient
```
