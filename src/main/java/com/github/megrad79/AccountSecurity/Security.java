package com.github.megrad79.AccountSecurity;

import com.datastax.oss.driver.api.core.CqlSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.megrad79.AccountSecurity.repository.ItemRepository;
import com.github.megrad79.AccountSecurity.service.ItemService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Security{
    public static void main(String [] args) throws URISyntaxException {
        Path file = Paths.get(Security.class.getResource("/error.html").toURI());

        CqlSession session = CqlSession.builder().build();
        ItemRepository itemRepository = new ItemRepository(session);
        ItemService itemService = new ItemService(itemRepository);

        DisposableServer server =
            HttpServer.create()   // Prep HTTP server for configuration
            //.host("localhost")
            .port(8080)

            // Routing HTTP per Section 5.3 example at
            // https://projectreactor.io/docs/netty/release/reference/index.html#_routing_http
            .route(routes ->
                    // Serves a GET request to /hello and returns Hello World!
                routes.get("/hello",
                            (request, response) -> response.sendString(Mono.just("Hello World!").log("http-server")))

                    // Serves a POST request to /echo and returns the received request body as a response.
                    .post("/echo",
                            (request, response) -> response.send(request.receive().retain().log("http-server")))

                    // Serves a GET request to /items/{param} and returns the value of the path parameter.
                    .get("/items/{param}",
                            (request, response) -> response.send(itemService.get(request.param("param")).map(Security::toByteBuf).log("http-server")))

                    // Serves a GET request to /path/{param} and returns the value of the path parameter.
                    .get("/items",
                            (request, response) -> response.send(itemService.getAllItems().map(Security::toByteBuf).log("http-server")))

                    // serves web socket to /ws and returns recieved incoming data as outgoing
                    .ws("/ws",
                            (wsInbound, wsOutbound) -> wsOutbound.send(wsInbound.receive().retain().log("http-server")))

                    // sends get request to /error and returns "Warning - missing content"
                    .get("/error",
                            (request, response) -> response.status(404).addHeader("Message","Warning - missing content").sendFile(file))
                )
                .bindNow();

        server.onDispose().block();
    }

    private static ByteBuf toByteBuf(Object any) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write("data: ".getBytes(Charset.defaultCharset()));
            MAPPER.writeValue(out, any);
            out.write("\n\n".getBytes(Charset.defaultCharset()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ByteBufAllocator.DEFAULT.buffer().writeBytes(out.toByteArray());
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
}