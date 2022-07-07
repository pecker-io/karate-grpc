Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def client = Java.type('demo.DemoGrpcClientSingleton').INSTANCE.getGrpcClient();

  Scenario: do it
    * string payload = read('list-features.json')
    * def response = client.call('helloworld.Greeter/ListFeatures', payload)
    * def response = JSON.parse(response)
    * match response[0].name == 'Patriots Path, Mendham, NJ 07945, USA'
