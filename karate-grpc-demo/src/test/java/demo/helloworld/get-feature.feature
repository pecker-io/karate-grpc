Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def client = Java.type('demo.DemoGrpcClientSingleton').INSTANCE.getGrpcClient();

  Scenario: do it
    * string payload = read('get-feature.json')
    * def response = client.call('helloworld.Greeter/GetFeature', payload)
    * def response = JSON.parse(response)
    * match response[0].name == '352 South Mountain Road, Wallkill, NY 12589, USA'
