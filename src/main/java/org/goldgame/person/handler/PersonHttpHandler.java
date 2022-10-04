package org.goldgame.person.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.person.model.Person;
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

        String query = exchange.getRequestURI().getQuery(); // ?=123 - запрос в браузерной строке

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    List<Person> persons = personService.getAll();
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson(persons).getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Got all persons list");
                        exchange.close();
                    }
                } else if (query.toLowerCase().contains("id=")) {
                    Long id = Long.parseLong(query.split("=")[1]);
                    Person person = personService.getPerson(id);
                    if (person == null) {
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
                            os.write(gson.toJson(person).getBytes(Config.DEFAULT_CHARSET));
                            os.flush();
                            log.info("Got person with id {}", id);
                            exchange.close();
                        }
                    }
                }
                break;
            }
            case "POST":{
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET);
                Person person = gson.fromJson(body, Person.class);

                if (person.getName() != null) {
                    personService.create(person);
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson("Person created").getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Person created");
                        exchange.close();
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Wrong request body".getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Name can't be null when creating a person");
                        exchange.close();
                    }
                }
                break;
            }
            case "PATCH": {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), Config.DEFAULT_CHARSET);
                Person person = personService.update(gson.fromJson(body, Person.class));
                if (person == null) {
                    exchange.sendResponseHeaders(404, 0);
                    log.info("Wrong id");
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(gson.toJson("Wrong request body").getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        exchange.close();
                    }
                    break;
                }
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(gson.toJson(person).getBytes(Config.DEFAULT_CHARSET));
                    os.flush();
                    log.info("Person has been updated {}", person);
                    exchange.close();
                }
                break;
            }
            case "DELETE": {
                Long id = Long.parseLong(query.split("=")[1]);
                if (personService.getPerson(id) == null) {
                    exchange.sendResponseHeaders(404, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Wrong requestBody".getBytes(Config.DEFAULT_CHARSET));
                        os.flush();
                        log.info("Person with id {} doesn't exist", id);
                        exchange.close();
                    }
                }
                personService.delete(id);
                exchange.sendResponseHeaders(201, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(("Person with id " + id + " has been deleted").getBytes(Config.DEFAULT_CHARSET));
                    os.flush();
                    log.info("Person with id {} has been deleted", id);
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
