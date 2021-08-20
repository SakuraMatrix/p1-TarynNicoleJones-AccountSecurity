package com.github.megrad79.AccountSecurity;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.metadata.Metadata;
import com.datastax.oss.driver.api.core.metrics.Metrics;
import com.datastax.oss.driver.api.core.session.Request;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.github.megrad79.AccountSecurity.service.ItemService;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Configuration
@ComponentScan
public class SecurityConfig {
    @Autowired
    ItemService itemService;

    @Bean
    public CqlSession session(){
        return new CqlSession() {
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
    }

    @Bean
    public DisposableServer server() throws URISyntaxException {
        Path error = Paths.get(Security.class.getResource("/error.html").toURI());
        Path index = Paths.get(Security.class.getResource("/index.html").toURI());

        return HttpServer.create()   // Prep HTTP server for configuration
            .port(8080)
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

                            // Serves a POST request to /items and returns all data.
                            .post("/Items",
                                    (request, response) -> response.send(request.receive().asString().map(Security::parseItem).map(itemService::create).map(Security::toByteBuf).log("http-server")))

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
            .bindNow();
    }
}
