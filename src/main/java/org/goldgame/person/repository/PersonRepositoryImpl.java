package org.goldgame.person.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.goldgame.exception.ValidationException;
import org.goldgame.person.model.Person;
import org.goldgame.utilities.SqlSetup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class PersonRepositoryImpl implements PersonRepository {

    QueryRunner qr = new QueryRunner();

    @Override
    public void create(Person personDto) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()){
            String checkQuery = "SELECT persons.name FROM persons WHERE NAME = ?"; // Проверка, занято ли имя
            String checkName = qr.query(conn, checkQuery, new BeanHandler<>(String.class), personDto.getName());
            if (checkName != null) {
                throw new ValidationException("This person name is already taken");
            }
            String query = "INSERT INTO persons(name) VALUES (?)";
            ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();
            qr.insert(conn, query, scalarHandler, personDto.getName());
        }
    }

    @Override
    public Person update(Person personDto) throws SQLException {
        return null;
    }

    @Override
    public void delete(Long id) throws SQLException {

    }

    @Override
    public Person getPerson(Long id) throws SQLException {
        return null;
    }

    @Override
    public List<Person> getAll() throws SQLException {
        return null;
    }
}
