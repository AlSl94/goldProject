package org.goldgame.goldoperation.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.goldoperation.model.GoldOperation;
import org.goldgame.goldoperation.repository.GoldOperationRepositoryImpl;
import org.goldgame.goldoperation.service.GoldOperationService;
import org.goldgame.utilities.Config;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class GoldOperationHttpHandler implements HttpHandler {

    private static final Gson gson = Config.gson;
    private final GoldOperationService goldOperationService;

    public GoldOperationHttpHandler() {
        goldOperationService = new GoldOperationService(new GoldOperationRepositoryImpl());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (query.toLowerCase().contains("id=")) {
                    Long id = Long.parseLong(query.split("=")[1]);
                    GoldOperation goldOperation = goldOperationService.getOperation(id);
                    serverOutput(exchange, 200, gson.toJson(goldOperation),
                            "Got info about gold operation with id " + id);
                }
                break;
            }
            case "POST": {
                Headers headers = exchange.getRequestHeaders();
                Long clanId = Long.parseLong(headers.getFirst("X-Clan-Id"));
                Long personId = Long.parseLong(headers.getFirst("X-Person-Id"));
                Long amount = Long.parseLong(query.split("=")[1].split("&")[0]);
                Boolean isWithdrawal = Boolean.parseBoolean(query.split("=")[2]);
                goldOperationService.goldOperation(clanId, personId, amount, isWithdrawal);
                serverOutput(exchange, 200, gson.toJson("Operation successful"),
                        "Operation successful");
                break;
            }
            default:
                serverOutput(exchange, 400, gson.toJson("Request must be: GET, PATCH"),
                        "Wrong request method: " + 400);
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