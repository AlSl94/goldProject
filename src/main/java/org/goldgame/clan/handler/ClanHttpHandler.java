package org.goldgame.clan.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.clan.dto.ClanDto;
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
                    List<ClanDto> clans = clanService.getAll();
                    serverOutput(exchange, 200, gson.toJson(clans), "Got all clans list");
                } else if (query.toLowerCase().contains("id=")) {
                    Long id = Long.parseLong(query.split("=")[1]);
                    ClanDto clan = clanService.getClan(id);
                    if (clan == null) {
                        serverOutput(exchange, 404, gson.toJson("Wrong id"), "Wrong id");
                    } else {
                        serverOutput(exchange, 200, gson.toJson(clan), "Got clan with id " + id);
                    }
                }
                break;
            }
            case "POST": {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET);
                ClanDto clan = gson.fromJson(body, ClanDto.class);

                if (clan.getName() != null) {
                    clanService.create(clan);
                    serverOutput(exchange, 200, gson.toJson("Clan created"), "Clan created");
                } else {
                    serverOutput(exchange, 404, gson.toJson("Wrong request body"), "Name can't be null when creating a clan");
                }
                break;
            }
            case "PATCH": {

                InputStream inputStream = exchange.getRequestBody();
                Long id = Long.parseLong(query.split("=")[1]);
                ClanDto updatedClan = gson.fromJson(new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET), ClanDto.class);
                ClanDto clan = clanService.update(id, updatedClan);

                if (clan == null) {
                    serverOutput(exchange, 404, gson.toJson("Wrong request body"), "Wrong id");
                    break;
                }

                serverOutput(exchange, 200, gson.toJson(clan), "Clan has been updated " + clan);
                break;
            }
            case "DELETE": {

                Long id = Long.parseLong(query.split("=")[1]);
                if (clanService.getClan(id) == null) {
                    serverOutput(exchange, 404, gson.toJson("Wrong request body"), "Wrong id");
                }
                clanService.delete(id);
                serverOutput(exchange, 200, gson.toJson("Clan with id " + id + "deleted, id"), "Clan with id " + id + "deleted, id");
                break;
            }
            default:
                serverOutput(exchange, 400, gson.toJson("Request must be: POST, GET, DELETE, PATCH"), "Wrong request method: " + 400);
        }
    }

    private void serverOutput(HttpExchange exchange, int code, String output, String logInfo) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(output.getBytes(Config.DEFAULT_CHARSET));
            os.flush();
            log.info(logInfo);
            exchange.close();
        }
    }
}