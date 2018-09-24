Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def config = { host: 'localhost', port: 50051, extra: 'other config information' }
    * def client = Client.create(config.host, config.port)

  Scenario: do it
    * def payload = read('helloworld.json')
    * def response = client.invoke('helloworld.Greeter/SayHello', payload)
    * def response = JSON.parse(response)
    * match response.message == 'Hello thinkerou'
