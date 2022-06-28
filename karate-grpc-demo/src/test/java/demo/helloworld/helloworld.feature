Feature: grpc helloworld example

  Background:
    * def Client = Java.type('com.github.thinkerou.demo.helloworld.HelloWorldClient')
    * def client = new Client('localhost', 50051)

  Scenario: do it
    * string payload = read('helloworld.json')
    * def response = client.greet(payload)
    * def response = JSON.parse(response)
    * match response.message == 'Hello thinkerou'
    * def message = response.message

    * string payload = read('again-helloworld.json')
    * def response = client.againGreet(payload)
    * eval client.shutdown()
    * def response = JSON.parse(response)
    * match response.details == 'Details Hello thinkerou in BeiJing'
