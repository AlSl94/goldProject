package org.goldgame.person.repository;

import org.goldgame.person.dto.PersonDto;

import java.sql.SQLException;
import java.util.List;

public interface PersonRepository {

    void create(PersonDto personDto) throws SQLException;

    PersonDto update(Long id, PersonDto personDto) throws SQLException;

    void delete(Long id) throws SQLException;

    PersonDto getPerson(Long id) throws SQLException;

    List<PersonDto> getAll() throws SQLException;

    void joinClan(Long clanId, Long personId) throws SQLException;
}
