Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = Client.create('localhost', 50051)

  Scenario: do it
    * def payload = read('get-feature.json')
    * def response = client.call('helloworld.Greeter/GetFeature', payload)
    * def response = JSON.parse(response)
    * match response[0].name == '352 South Mountain Road, Wallkill, NY 12589, USA'
