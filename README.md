# karate-grpc
gRPC Testing Made Simple by Karate.

## testing helloworld

Prefer to use Maven:

```
$ mvn verify
$ # Run the server
$ mvn exec:java -Dexec.mainClass=example.helloworld.HelloWorldServer
$ # In another terminal run the client
$ mvn exec:java -Dexec.mainClass=example.helloworld.HelloWorldClient
```
