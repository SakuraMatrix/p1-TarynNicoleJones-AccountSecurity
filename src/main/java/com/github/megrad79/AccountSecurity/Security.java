package com.github.megrad79.AccountSecurity;

import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Security{
    public static void main(String [] args) {
        //Testing Compile
        // System.out.println("Taryn's World!");

        /*// File printout example
        File f = new File("standup2.txt");
        Scanner s = new Scanner(f);

        while(s.hasNextLine()){
            System.out.println(s.nextLine());
        }*/

        DisposableServer server =
        HttpServer.create()   // Prepares an HTTP server ready for configuration
                //.host("localhost")
                .port(8080)    // Configures the port number as 8080, this will let the system pick up
                // an ephemeral port when binding the server

                // Routing HTTP per Section 5.3 example at
                // https://projectreactor.io/docs/netty/release/reference/index.html#_routing_http
                .route(routes ->
                        routes.get("/hello",
                                        (request, response) -> response.sendString(Mono.just("Hello World!")))
                                .post("/echo",
                                        (request, response) -> response.send(request.receive().retain()))
                                .get("/path/{param}",
                                        (request, response) -> response.sendString(Mono.just(request.param("param"))))
                                .ws("/ws",
                                        (wsInbound, wsOutbound) -> wsOutbound.send(wsInbound.receive().retain())))
                .bindNow();


                       /* // The server will respond only on POST requests
                        // where the path starts with /test and then there is path parameter
                        routes.post("/test/{param}", (request, response) ->
                                response.sendString(request.receive()
                                        .asString()
                                        .map(s -> s + ' ' + request.param("param") + '!')
                                        .log("http-server"))))
                .bindNow(); // Starts the server in a blocking fashion, and waits for it to finish its initialization
*/
        server.onDispose().block();

    }
}