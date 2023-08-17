Feature: grpc helloworld example using base GrpcCient

  Background:
    * def Client = Java.type('com.github.thinkerou.karate.GrpcClient')
    * def client = new Client('localhost', 50051)

  Scenario: do it
    * def testHeaderValue = 'testing gRPC headers'
    * configure headers = { 'karate-test-header': "#(testHeaderValue)" }
    * string payload = read('helloworld.json')
    * def response = client.call("helloworld.Greeter/SayHello", payload, karate)
    # I'm not sure why this isn't exposed in Karate's "match header", but this suits
    # for our immediate needs of supporting gRPC client headers.
    * assert responseHeaders['karate-test-server-header'][0] == testHeaderValue
    * def reply = JSON.parse(response)
    * match reply[0].message == 'Hello thinkerou'
    * def message = reply[0].message

    * string payload2 = read('again-helloworld.json')
    * def response2 = client.call("helloworld.Greeter/AgainSayHello", payload2, karate)
    * def reply2 = JSON.parse(response2)
    * match reply2[0].details == 'Details Hello thinkerou in BeiJing'
