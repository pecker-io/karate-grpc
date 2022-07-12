Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def client = Java.type('demo.DemoGrpcClientSingleton').INSTANCE.getGrpcClient();

  Scenario: do it
    * string payload = read('helloworld.json')
    * def response = client.call('helloworld.Greeter/SayHello', payload, karate)
    * def response = JSON.parse(response)
    * print response
    * match response[0].message == 'Hello thinkerou'
    * def message = response[0].message

    * string payload = read('again-helloworld.json')
    * def response = client.call('helloworld.Greeter/AgainSayHello', payload, karate)
    * def response = JSON.parse(response)
    * match response[0].details == 'Details Hello thinkerou in BeiJing'
