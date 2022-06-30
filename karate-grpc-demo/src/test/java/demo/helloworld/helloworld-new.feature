Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)
    * def client = client.redis()

  Scenario: do it
    * string payload = read('helloworld.json')
    * def response = client.call('helloworld.Greeter/SayHello', payload)
    * def response = JSON.parse(response)
    * match response[0].message == 'Hello thinkerou'
    * def message = response[0].message

    * string payload = read('again-helloworld.json')
    * def response = client.call('helloworld.Greeter/AgainSayHello', payload)
    * def response = JSON.parse(response)
    * match response[0].details == 'Details Hello thinkerou in BeiJing'
