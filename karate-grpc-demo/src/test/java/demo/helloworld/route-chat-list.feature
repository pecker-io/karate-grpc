Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create()
    * def client = client.redis()

  Scenario: do it
    * def response = client.list('Greeter', 'RouteChat', true)
    * def response = JSON.parse(response)
    * print response
