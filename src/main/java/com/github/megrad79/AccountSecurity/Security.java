package com.github.megrad79.AccountSecurity;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.metadata.Metadata;
import com.datastax.oss.driver.api.core.metrics.Metrics;
import com.datastax.oss.driver.api.core.session.Request;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.megrad79.AccountSecurity.domain.Item;
import com.github.megrad79.AccountSecurity.repository.ItemRepository;
import com.github.megrad79.AccountSecurity.service.ItemService;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.datastax.oss.driver.api.core.CqlSession.builder;


public class Security{
    public static void main(String [] args) throws URISyntaxException {

        /*CqlSession session = new CqlSession() {
            @NonNull
            @Override
            public String getName() {
                return null;
            }

            @NonNull
            @Override
            public Metadata getMetadata() {
                return null;
            }

            @Override
            public boolean isSchemaMetadataEnabled() {
                return false;
            }

            @NonNull
            @Override
            public CompletionStage<Metadata> setSchemaMetadataEnabled(@Nullable Boolean aBoolean) {
                return null;
            }

            @NonNull
            @Override
            public CompletionStage<Metadata> refreshSchemaAsync() {
                return null;
            }

            @NonNull
            @Override
            public CompletionStage<Boolean> checkSchemaAgreementAsync() {
                return null;
            }

            @NonNull
            @Override
            public DriverContext getContext() {
                return null;
            }

            @NonNull
            @Override
            public Optional<CqlIdentifier> getKeyspace() {
                return Optional.empty();
            }

            @NonNull
            @Override
            public Optional<Metrics> getMetrics() {
                return Optional.empty();
            }

            @Nullable
            @Override
            public <RequestT extends Request, ResultT> ResultT execute(@NonNull RequestT requestT, @NonNull GenericType<ResultT> genericType) {
                return null;
            }

            @NonNull
            @Override
            public CompletionStage<Void> closeFuture() {
                return null;
            }

            @NonNull
            @Override
            public CompletionStage<Void> closeAsync() {
                return null;
            }

            @NonNull
            @Override
            public CompletionStage<Void> forceCloseAsync() {
                return null;
            }
        };
        ItemRepository itemRepository = new ItemRepository(session);
        ItemService itemService = new ItemService(itemRepository);
*/
        /*DisposableServer server =
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

                    // Serves a GET request to /items and returns all data.
                    .get("/items",
                            (request, response) -> response.send(itemService.getAllItems().map(Security::toByteBuf).log("http-server")))

                    // serves web socket to /ws and returns received incoming data as outgoing
                    .ws("/ws",
                            (wsInbound, wsOutbound) -> wsOutbound.send(wsInbound.receive().retain().log("http-server")))

                    // sends get request to /error and returns "Warning - missing content"
                    .get("/",
                            (request, response) -> response.sendFile(index))

                    // sends get request to /error and returns "Warning - missing content"
                    .get("/error",
                            (request, response) -> response.status(404).addHeader("Message","Warning - missing content").sendFile(error))
                )
                .bindNow();*/
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityConfig.class);

        context.getBean(DisposableServer.class).onDispose().block();
    }

    static ByteBuf toByteBuf(Object any) {
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

    static final ObjectMapper MAPPER = new ObjectMapper();

    static Item parseItem(String s){
        Item item = null;

        try{
            item = MAPPER.readValue(s, Item.class);
        }
        catch (JsonProcessingException ex){
            String[] params = s.split("&");
            int id = Integer.parseInt(params[0].split("=")[1]);
            String name = params[1].split("=")[1];
            double price = Double.parseDouble(params[2].split("=")[1]);

            item = new Item(id, name, price);
        }
        return item;
    }
}