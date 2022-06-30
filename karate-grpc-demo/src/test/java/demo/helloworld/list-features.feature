Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)
    * def client = client.redis()

  Scenario: do it
    * string payload = read('list-features.json')
    * def response = client.call('helloworld.Greeter/ListFeatures', payload)
    * def response = JSON.parse(response)
    * match response[0].name == 'Patriots Path, Mendham, NJ 07945, USA'
