Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)
    * def client = client.redis('localhost', 6379)

  Scenario: do it
    * def payload = read('bistream.json')
    * def response = client.call('helloworld.Greeter/SayHelloBiStreaming', payload)
    * def response = JSON.parse(response)
    * match response[*].message == ['Hello thinkerou', 'Hello thinkerou2', 'Hello thinkerou3']