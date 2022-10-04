package org.goldgame.clan.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.clan.model.Clan;
import org.goldgame.clan.repository.ClanRepositoryImpl;
import org.goldgame.clan.service.ClanService;
import org.goldgame.utilities.Config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class ClanHttpHandler implements HttpHandler {

    private final ClanService clanService;
    private static final Gson gson = Config.gson;

    public ClanHttpHandler() {
        clanService = new ClanService(new ClanRepositoryImpl());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery(); // ?=123 - запрос в браузерной строке

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    List<Clan> clans = clanService.getAll();
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson(clans).getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Got all clans list");
                        exchange.close();
                    }
                } else if (query.toLowerCase().contains("id=")) {

                    Long id = Long.parseLong(query.split("=")[1]);
                    Clan clan = clanService.getClan(id);

                    if (clan == null) {
                        exchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write("Wrong id".getBytes(Config.DEFAULT_CHARSET));
                            os.flush();
                            log.info("Wrong id");
                            exchange.close();
                        }
                    } else {
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(gson.toJson(clan).getBytes(Config.DEFAULT_CHARSET));
                            os.flush();
                            log.info("Got clan with id {}", id);
                            exchange.close();
                        }
                    }
                }
                break;
            }
            case "POST": {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET);
                Clan clan = gson.fromJson(body, Clan.class);

                if (clan.getName() != null) {
                    clanService.create(clan);
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson("Clan created").getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Clan created");
                        exchange.close();
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Wrong request body".getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Name can't be null when creating a clan");
                        exchange.close();
                    }
                }
                break;
            }
            case "PATCH": {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET);
                Clan clan = clanService.update(gson.fromJson(body, Clan.class));
                if (clan == null) {
                    exchange.sendResponseHeaders(404, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson("Wrong request body").getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Wrong id");
                        exchange.close();
                    }
                    break;
                }
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(gson.toJson(clan).getBytes(Config.DEFAULT_CHARSET));
                    os.flush();
                    log.info("Clan has been updated {}", clan);
                    exchange.close();
                }
                break;
            }
            case "DELETE": {
                Long id = Long.parseLong(query.split("=")[1]);
                if (clanService.getClan(id) == null) {
                    exchange.sendResponseHeaders(404, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Wrong requestBody".getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Clan with id {} doesn't exist", id);
                        exchange.close();
                    }
                }
                clanService.delete(id);

                exchange.sendResponseHeaders(201, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(("Clan deleted, id# " + id).getBytes(Config.DEFAULT_CHARSET));
                    os.flush();
                    log.info("Clan deleted, id {}", id);
                    exchange.close();
                }
                break;
            }
            default:
                exchange.sendResponseHeaders(400, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("Request must be: POST, GET, DELETE, PATCH".getBytes(Config.DEFAULT_CHARSET));
                    os.flush();
                    log.info("Wrong request method {}", 400);
                    exchange.close();
                }
        }
    }
}