package org.goldgame.httpServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.clan.handler.ClanHttpHandler;
import org.goldgame.person.handler.PersonHttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@Slf4j
public class HttpGameServer {

    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpGameServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/hello", new HelloHandler());
        server.createContext("/clans", new ClanHttpHandler());
        server.createContext("/persons", new PersonHttpHandler());
    }

    public void load() {
        server.setExecutor(null); // creates a default executor
        server.start();
        log.info("Server started...");
    }

    public static class HelloHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String respText = "Hello!";
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(respText.getBytes());
            os.flush();
            exchange.close();
            log.info("Получен ответ от /api/hello");
        }
    }
}
