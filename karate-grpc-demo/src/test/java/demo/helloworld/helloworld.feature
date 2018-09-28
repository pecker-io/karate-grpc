Feature: grpc helloworld example

  Background:
    * def Client = Java.type('com.github.thinkerou.demo.helloworld.HelloWorldClient')
    * def client = new Client('localhost', 50051)

  Scenario: do it
    * def payload = read('helloworld.json')
    * print payload
    * def response = client.greet(payload)
    * def response = JSON.parse(response)
    * match response.message == 'Hello thinkerou'

    * def payload = read('again-helloworld.json')
    * print payload
    * def response = client.againGreet(payload)
    * eval client.shutdown()
    * def response = JSON.parse(response)
    * print response
