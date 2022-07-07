Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def client = Java.type('demo.DemoGrpcClientSingleton').INSTANCE.getGrpcClient();

  Scenario: do it
    * string payload = read('record-route.json')
    * def response = client.call('helloworld.Greeter/RecordRoute', payload)
    * def response = JSON.parse(response)
    * match response == '#[1]'
    * match response[0] == { 'point_count': 2, 'feature_count': 2, 'distance': 72948, 'elapsed_time': 0 }
