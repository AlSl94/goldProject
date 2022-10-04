package org.goldgame.person.repository;

import org.goldgame.person.model.Person;

import java.sql.SQLException;
import java.util.List;

public interface PersonRepository {

    void create(Person personDto) throws SQLException;

    Person update(Person personDto) throws SQLException;

    void delete(Long id) throws SQLException;

    Person getPerson(Long id) throws SQLException;

    List<Person> getAll() throws SQLException;

}
