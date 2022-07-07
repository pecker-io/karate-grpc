Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def client = Java.type('demo.DemoGrpcClientSingleton').INSTANCE.getGrpcClient();

  Scenario: do it
    * string payload = read('bistream.json')
    * def response = client.call('helloworld.Greeter/SayHelloBiStreaming', payload)
    * def response = JSON.parse(response)
    * match response[*].message == ['Hello thinkerou', 'Hello thinkerou2', 'Hello thinkerou3']