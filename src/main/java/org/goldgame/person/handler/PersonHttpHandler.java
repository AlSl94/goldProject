package org.goldgame.person.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.person.dto.PersonDto;
import org.goldgame.person.repository.PersonRepositoryImpl;
import org.goldgame.person.service.PersonService;
import org.goldgame.utilities.Config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class PersonHttpHandler implements HttpHandler {
    private final PersonService personService;
    private static final Gson gson = Config.gson;

    public PersonHttpHandler() {
        personService = new PersonService(new PersonRepositoryImpl());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    List<PersonDto> persons = personService.getAll();
                    serverOutput(exchange, 200, gson.toJson(persons), "Got all persons list");
                } else if (query.toLowerCase().contains("id=")) {
                    Long id = Long.parseLong(query.split("=")[1]);
                    PersonDto person = personService.getPerson(id);
                    if (person == null) {
                        serverOutput(exchange, 404, "Wrong id", "Wrong id");
                    } else {
                        serverOutput(exchange, 200, gson.toJson(person), "Got person with id " + id);
                    }
                }
                break;
            }
            case "POST": {
                if (query == null) {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET);
                    PersonDto person = gson.fromJson(body, PersonDto.class);
                    if (person.getName() != null) {
                        personService.create(person);
                        serverOutput(exchange, 200, gson.toJson("Person created"), "Person created");
                    } else {
                        serverOutput(exchange, 404, gson.toJson("Wrong request body"), "Name can't be null when creating a person");
                    }
                } else if (query.contains("clanId")) {
                    Long personId = Long.parseLong(query.split("=")[1].split("&")[0]);
                    Long clanId = Long.parseLong(query.split("=")[2]);
                    personService.joinClan(clanId, personId);
                    serverOutput(exchange, 200, gson.toJson("Person created"), "Person created");
                }
                break;
            }
            case "PATCH": {
                InputStream inputStream = exchange.getRequestBody();
                Long id = Long.parseLong(query.split("=")[1]);
                PersonDto personDto = gson.fromJson(new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET), PersonDto.class);
                PersonDto person = personService.update(id, personDto);

                if (person == null) {
                    serverOutput(exchange, 404, gson.toJson("Wrong request body"), "Wrong id");
                    break;
                }

                serverOutput(exchange, 200, gson.toJson(person), "Person has been updated " + person.getName());
                break;
            }
            case "DELETE": {
                Long id = Long.parseLong(query.split("=")[1]);
                if (personService.getPerson(id) == null) {
                    serverOutput(exchange, 404, gson.toJson("Wrong request body"), "Wrong id");
                }
                personService.delete(id);
                serverOutput(exchange, 200, gson.toJson("Person with id " + id + " has been deleted"), "Person with id " + id + " has been deleted");
                break;
            }
            default:
                serverOutput(exchange, 400, gson.toJson("Request must be: POST, GET, DELETE, PATCH"), "Wrong request method " + 400);
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
