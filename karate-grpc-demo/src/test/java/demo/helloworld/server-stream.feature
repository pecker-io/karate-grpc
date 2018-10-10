Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)

  Scenario: do it
    * def payload = read('serverstream.json')
    * def response = client.call('helloworld.Greeter/SayHelloServerStreaming', payload)
    * def response = JSON.parse(response)
    * match response[*].message == ['Hello thinkerou part 0', 'Hello thinkerou part 1', 'Hello thinkerou part 2']
