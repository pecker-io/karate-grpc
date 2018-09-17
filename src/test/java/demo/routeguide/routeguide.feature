Feature: grpc routeguide example

  Background:
    * def Client = Java.type('example.routeguide.RouteGuideClient')
    * def config = { host: 'localhost', port: 50052, extra: 'other config information' }
    * def client = new Client(config.host, config.port)

  Scenario: do it
    * def payload = read('routeguide.json')
