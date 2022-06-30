Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)
    * def client = client.redis()

  Scenario: do it
    * string payload = read('route-chat.json')
    * def response = client.call('helloworld.Greeter/RouteChat', payload)
    * def response = JSON.parse(response)
    * print response
