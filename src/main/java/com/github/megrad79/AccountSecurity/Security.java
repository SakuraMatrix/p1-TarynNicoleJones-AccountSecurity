package com.github.megrad79.AccountSecurity;

import com.github.megrad79.AccountSecurity.repository.ItemRepository;
import com.github.megrad79.AccountSecurity.service.ItemService;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Security{
    public static void main(String [] args) throws URISyntaxException {
        Path file = Paths.get(Security.class.getResource("/error.html").toURI());

        ItemRepository itemRepository = new ItemRepository();
        ItemService itemService = new ItemService(itemRepository);

        DisposableServer server =
            HttpServer.create()   // Prep HTTP server for configuration
            //.host("localhost")
            .port(8080)

            // Routing HTTP per Section 5.3 example at
            // https://projectreactor.io/docs/netty/release/reference/index.html#_routing_http
            .route(routes ->
                routes.get("/hello",
                            (request, response) -> response.sendString(Mono.just("Hello World!").log("http-server")))
                    .post("/echo",
                            (request, response) -> response.send(request.receive().retain().log("http-server")))
                    .get("/path/{param}",
                            (request, response) -> response.sendString(Mono.just(request.param("param")).log("http-server")))
                    .ws("/ws",
                            (wsInbound, wsOutbound) -> wsOutbound.send(wsInbound.receive().retain().log("http-server")))
                    .get("/test",
                            (request, response) -> response.status(404).addHeader("Message","Warning - missing content").sendFile(file))
                )
                .bindNow();

        server.onDispose().block();

    }
}