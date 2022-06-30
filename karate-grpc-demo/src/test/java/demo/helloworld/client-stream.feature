Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)
    * def client = client.redis()

  Scenario: do it
    * string payload = read('clientstream.json')
    * def response = client.call('helloworld.Greeter/SayHelloClientStreaming', payload)
    * def response = JSON.parse(response)
    * match response[0].message == 'Hello thinkerou and thinkerou2 and thinkerou3'
