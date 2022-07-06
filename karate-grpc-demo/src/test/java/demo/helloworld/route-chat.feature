Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def client = Java.type('demo.DemoGrpcClientSingleton').INSTANCE.getGrpcClient();

  Scenario: do it
    * string payload = read('route-chat.json')
    * def response = client.call('helloworld.Greeter/RouteChat', payload)
    * def response = JSON.parse(response)
    * print response
