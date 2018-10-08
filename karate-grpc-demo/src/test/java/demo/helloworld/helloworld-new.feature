Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)

  Scenario: do it
    * def payload = read('helloworld.json')
    * def response = client.call('helloworld.Greeter/SayHello', payload)
    * def response = JSON.parse(response)
    * print response
    * match response.message == 'Hello thinkerou'

    * def payload = read('again-helloworld.json')
    * def response = client.call('helloworld.Greeter/AgainSayHello', payload)
    * def response = JSON.parse(response)
    * match response.details == 'Details Hello thinkerou in BeiJing'
