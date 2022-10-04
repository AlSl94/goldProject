package org.goldgame.person.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.person.model.Person;
import org.goldgame.person.repository.PersonRepository;
import org.goldgame.utilities.SqlSetup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public void create(Person personDto) {
        try {
            personRepository.create(personDto);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
    }

    public Person update(Person personDto) {
        Person person = personDto;
        try {
            person = personRepository.update(personDto);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return person;
    }

    public void delete(Long id)  {
        try {
            personRepository.delete(id);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
    }

    public Person getPerson(Long id) {
        Person person = null;
        try {
            person = personRepository.getPerson(id);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return person;
    }

    public List<Person> getAll() {
        List<Person> persons = new ArrayList<>();
        try {
            persons = personRepository.getAll();
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return persons;
    }
}
