Feature: grpc helloworld example

  Background:
    * def Client = Java.type('example.helloworld.HelloWorldClient')
    * def config = { host: 'localhost', port: 50051, extra: 'other config information' }
    * def client = new Client(config.host, config.port)

  Scenario: do it
    * def payload = read('helloworld.json')
    * def response = client.greet(payload)
    * eval client.shutdown()
    * def response = JSON.parse(response)
    * match response.message == 'Hello thinkerou'
