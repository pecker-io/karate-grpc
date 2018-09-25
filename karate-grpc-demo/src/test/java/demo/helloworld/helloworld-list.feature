Feature: grpc helloworld example by grpc dynamic client

  Background:
    * def List = Java.type('com.github.thinkerou.karate.GrpcList')

  Scenario: do it
    * def response = List.invoke('Greeter', 'SayHello')
    * print response
    * match response.message contains 'SayHello'
