Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def client = Java.type('demo.DemoGrpcClientSingleton').INSTANCE.getGrpcClient();

  Scenario: do it
    * string payload = read('serverstream.json')
    * def response = client.call('helloworld.Greeter/SayHelloServerStreaming', payload, karate)
    * def response = JSON.parse(response)
    * match response[*].message == ['Hello thinkerou part 0', 'Hello thinkerou part 1', 'Hello thinkerou part 2']
