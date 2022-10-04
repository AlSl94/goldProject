package org.goldgame.person.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.goldgame.clan.model.Clan;
import org.goldgame.exception.ValidationException;
import org.goldgame.exception.WrongParameterException;
import org.goldgame.person.dto.PersonDto;
import org.goldgame.person.model.Person;
import org.goldgame.utilities.SqlSetup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class PersonRepositoryImpl implements PersonRepository {

    QueryRunner qr = new QueryRunner();

    @Override
    public void create(PersonDto personDto) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()){
            String checkQuery = "SELECT persons.name FROM persons WHERE NAME = ?"; // Проверка, занято ли имя
            String checkName = qr.query(conn, checkQuery, new BeanHandler<>(String.class), personDto.getName());
            if (checkName != null) {
                throw new ValidationException("This person name is already taken");
            }
            if (personDto.getName() == null) {
                throw new ValidationException("Person name can't be null");
            }
            if (personDto.getGold() == null) {
                personDto.setGold(0L);
            }
            String query = "INSERT INTO persons(name, gold) VALUES (?, ?)";
            ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();
            qr.insert(conn, query, scalarHandler, personDto.getName(), personDto.getGold());
        }
    }

    @Override
    public PersonDto update(Long id, PersonDto personDto) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            PersonDto person = getPerson(id);
            if (person == null) {
                throw new ValidationException("Wrong clan id");
            }
            if (personDto.getName() != null) person.setName(personDto.getName());
            if (personDto.getGold() != null) person.setGold(personDto.getGold());
            String query = "UPDATE persons SET name = ?, gold = ? WHERE id = ?";
            qr.update(conn, query, person.getName(), person.getGold(), id);
            return person;
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            String query = "DELETE FROM PERSONS WHERE id = ?";
            qr.update(conn, query, id);
        }
    }

    @Override
    public PersonDto getPerson(Long id) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            String query = "SELECT p.name, p.GOLD FROM persons p WHERE ID = ?";
            BeanHandler<PersonDto> beanHandler = new BeanHandler<>(PersonDto.class);
            return qr.query(conn, query, beanHandler, id);
        }
    }

    @Override
    public List<PersonDto> getAll() throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            String query = "SELECT p.name, p.gold FROM PERSONS p";
            BeanListHandler<PersonDto> beanListHandler = new BeanListHandler<>(PersonDto.class);
            return qr.query(conn, query, beanListHandler);
        }
    }

    @Override
    public void joinClan(Long clanId, Long personId) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {

            String checkClanQuery = "SELECT c.id, c.NAME, c.gold FROM clans c WHERE c.id = ?";
            BeanHandler<Clan> clanHandler = new BeanHandler<>(Clan.class);
            Clan clan = qr.query(conn, checkClanQuery, clanHandler, clanId);
            if (clan == null) {
                throw new WrongParameterException("Wrong clan id");
            }

            String checkPersonQuery = "SELECT p.id, p.name, p.gold FROM persons p WHERE p.id = ?";
            BeanHandler<Person> personHandler = new BeanHandler<>(Person.class);
            Person person = qr.query(conn, checkPersonQuery, personHandler, personId);
            if (person == null) {
                throw new WrongParameterException("Wrong person id");
            }

            String query = "INSERT INTO clan_person(clan_id, person_id) VALUES (?, ?)";
            ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();
            qr.insert(conn, query, scalarHandler, clanId, personId);
        }
    }
}
