Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)

  Scenario: do it
    * def payload = read('record-route.json')
    * def response = client.call('helloworld.Greeter/RecordRoute', payload)
    * def response = JSON.parse(response)
    * match response[0] == { 'pointCount': 2, 'featureCount': 2, 'distance': 72948 }
