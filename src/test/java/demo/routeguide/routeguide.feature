Feature: grpc routeguide example

  Background:
    * def Client = Java.type('example.helloworld.RouteGuideClient')
    * def config = { host: 'localhost', port: 50052, extra: 'other config information' }
    * def client = new Client(config.host, config.port)

  Scenario: do it
    * def payload = read('routeguide.json')
    * def response = client.getFeature(payload)
    * eval client.shutdown()
    * def response = JSON.parse(response)
    * match response.message == 'Hello thinkerou'
