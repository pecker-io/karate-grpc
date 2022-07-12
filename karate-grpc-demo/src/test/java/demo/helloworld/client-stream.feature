Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def client = Java.type('demo.DemoGrpcClientSingleton').INSTANCE.getGrpcClient();

  Scenario: do it
    * string payload = read('clientstream.json')
    * def response = client.call('helloworld.Greeter/SayHelloClientStreaming', payload, karate)
    * def response = JSON.parse(response)
    * match response[0].message == 'Hello thinkerou and thinkerou2 and thinkerou3'
