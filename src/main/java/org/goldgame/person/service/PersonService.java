package org.goldgame.person.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.person.dto.PersonDto;
import org.goldgame.person.repository.PersonRepository;
import org.goldgame.utilities.SqlSetup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public void create(PersonDto personDto) {
        try {
            personRepository.create(personDto);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
    }

    public PersonDto update(Long id, PersonDto personDto) {
        PersonDto person = personDto;
        try {
            person = personRepository.update(id, personDto);
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

    public PersonDto getPerson(Long id) {
        PersonDto person = null;
        try {
            person = personRepository.getPerson(id);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return person;
    }

    public List<PersonDto> getAll() {
        List<PersonDto> persons = new ArrayList<>();
        try {
            persons = personRepository.getAll();
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return persons;
    }

    public void joinClan(Long clanId, Long personId) {
        try {
            personRepository.joinClan(clanId, personId);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
    }
}
