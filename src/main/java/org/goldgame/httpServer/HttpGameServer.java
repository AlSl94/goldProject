package org.goldgame.httpServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.model.Clan;
import org.goldgame.repository.ClanRepositoryImpl;
import org.goldgame.service.ClanService;
import org.goldgame.utilities.Config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class HttpGameServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private static ClanService clanService;
    private static final Gson gson = Config.gson;

    public HttpGameServer() throws IOException {
        clanService = new ClanService(new ClanRepositoryImpl());
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/hello", new HelloHandler());
        server.createContext("/clans", new ClanHandler());
    }

    public void load() {
        server.setExecutor(null); // creates a default executor
        server.start();
        log.info("Server started...");
    }

    public static class ClanHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            InputStream inputStream;
            String body;
            Clan clan;

            String query = exchange.getRequestURI().getQuery(); // ?=123 - запрос в браузерной строке

            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        List<Clan> clans = clanService.getAll();
                        exchange.sendResponseHeaders(200, 0);
                        log.info("Получен список всех кланов");
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(gson.toJson(clans).getBytes(Config.DEFAULT_CHARSET));
                            os.flush();
                            exchange.close();
                        }
                    } else if (query.toLowerCase().contains("id=")) {

                        Long id = Long.parseLong(query.split("=")[1]);
                        clan = clanService.getClan(id);

                        if (clan == null) {
                            exchange.sendResponseHeaders(404, 0);
                            log.info("Неверный id");
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write("Неверный id".getBytes(Config.DEFAULT_CHARSET));
                                os.flush();
                                exchange.close();
                            }
                        } else {
                            exchange.sendResponseHeaders(200, 0);
                            log.info("Получен клан с id {}", id);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(gson.toJson(clan).getBytes(Config.DEFAULT_CHARSET));
                                os.flush();
                                exchange.close();
                            }
                        }
                    }
                    break;
                case "POST":
                    inputStream = exchange.getRequestBody();
                    body = new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET);
                    clan = gson.fromJson(body, Clan.class);

                    if (clan.getName() != null) {
                        clanService.create(clan);
                        exchange.sendResponseHeaders(200, 0);
                        log.info("Clan created");
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(gson.toJson("Clan created").getBytes(Config.DEFAULT_CHARSET));
                            os.flush();
                            exchange.close();
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                        log.info("Name can't be null when creating a clan");
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write("Неправильное тело запроса".getBytes(Config.DEFAULT_CHARSET));
                            os.flush();
                            exchange.close();
                        }
                    }
                    break;
                case "PATCH":
                    inputStream = exchange.getRequestBody();
                    body = new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET);
                    clan = clanService.update(gson.fromJson(body, Clan.class));
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson(clan).getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        exchange.close();
                    } if (clan == null) {
                        exchange.sendResponseHeaders(404, 0);
                        log.info("Не указано имя при создании клана");
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(gson.toJson("Неправильное тело запроса").getBytes(Config.DEFAULT_CHARSET));
                            os.flush();
                            exchange.close();
                        }
                        break;
                    }

                    exchange.sendResponseHeaders(200, 0);
                    log.info("Клан обновлен {}", clan);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson(clan).getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        exchange.close();
                    }
                    break;

                case "DELETE":
                    Long id = Long.parseLong(query.split("=")[1]);

                    if (clanService.getClan(id) == null) {
                        exchange.sendResponseHeaders(404, 0);
                        log.info("Clan with id {} doesn't exist", id);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write("Wrong requestBody".getBytes(Config.DEFAULT_CHARSET));
                            os.flush();
                            exchange.close();
                        }
                    }

                    clanService.delete(id);
                    log.info("Clan deleted, id {}", id);
                    exchange.sendResponseHeaders(201, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(("Clan deleted, id# " + id).getBytes(Config.DEFAULT_CHARSET));
                    }
                    break;

                default:
                    log.info("Wrong request, {}", 404);
                    exchange.sendResponseHeaders(404, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Request must be: POST, GET, DELETE, PATCH".getBytes(Config.DEFAULT_CHARSET));
                    }
            }
        }
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
