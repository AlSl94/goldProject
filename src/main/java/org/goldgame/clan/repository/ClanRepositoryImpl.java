package org.goldgame.clan.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.goldgame.clan.dto.ClanDto;
import org.goldgame.exception.ValidationException;
import org.goldgame.person.dto.PersonDto;
import org.goldgame.utilities.SqlSetup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ClanRepositoryImpl implements ClanRepository {
    private final QueryRunner qr = new QueryRunner();

    @Override
    public void create(ClanDto clanDto) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {

            String checkQuery = "SELECT c.name FROM clans c WHERE c.name = ?"; // Проверка, занято ли имя
            String checkName;

            try {
                checkName = qr.query(conn, checkQuery, new BeanHandler<>(ClanDto.class), clanDto.getName())
                        .getName();
            } catch (NullPointerException e) {
                checkName = null;
            }

            if (checkName != null) throw new ValidationException("This clan name is already taken");
            if (clanDto.getGold() == null) clanDto.setGold(0L);

            String query = "INSERT INTO clans(name, gold) VALUES (?,?)";
            ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();
            qr.insert(conn, query, scalarHandler, clanDto.getName(), clanDto.getGold());
        }
    }

    @Override
    public ClanDto update(Long id, ClanDto clanDto) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            ClanDto clan = getClan(id);
            if (clan == null) {
                throw new ValidationException("Wrong clan id");
            }
            if (clanDto.getName() != null) clan.setName(clanDto.getName());
            if (clanDto.getGold() != null) clan.setGold(clanDto.getGold());
            String query = "UPDATE clans SET name = ?, gold = ? WHERE id = ?";
            qr.update(conn, query, clan.getName(), clan.getGold(), id);
            return clan;
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            String query = "DELETE FROM CLANS WHERE id = ?";
            qr.update(conn, query, id);
        }
    }

    @Override
    public ClanDto getClan(Long id) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            String query = "SELECT c.name, c.GOLD FROM clans c WHERE ID = ?";
            BeanHandler<ClanDto> clanHandler = new BeanHandler<>(ClanDto.class);
            ClanDto clan = qr.query(conn, query, clanHandler, id);
            if (clan != null) {
                String populationQuery = "SELECT p.NAME, p.GOLD FROM PERSONS p " +
                        "JOIN CLAN_PERSON cp ON cp.PERSON_ID = p.ID " +
                        "WHERE cp.CLAN_ID = ?";
                BeanListHandler<PersonDto> personHandler = new BeanListHandler<>(PersonDto.class);
                List<PersonDto> population = qr.query(conn, populationQuery, personHandler, id);
                clan.setPopulation(population);
            }
            return clan;
        }
    }

    @Override
    public List<ClanDto> getAll() throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            String query = "SELECT c.NAME, c.GOLD FROM CLANS c";
            BeanListHandler<ClanDto> handler = new BeanListHandler<>(ClanDto.class);
            return qr.query(conn, query, handler);
        }
    }
}