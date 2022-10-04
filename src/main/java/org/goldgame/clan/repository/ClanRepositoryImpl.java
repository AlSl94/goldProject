package org.goldgame.clan.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.goldgame.exception.ValidationException;
import org.goldgame.clan.model.Clan;
import org.goldgame.utilities.SqlSetup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ClanRepositoryImpl implements ClanRepository {
    QueryRunner qr = new QueryRunner();

    @Override
    public void create(Clan clanDto) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()){
            String checkQuery = "SELECT clans.name FROM CLANS WHERE NAME = ?"; // Проверка, занято ли имя
            String checkName = qr.query(conn, checkQuery, new BeanHandler<>(String.class), clanDto.getName());
            if (checkName != null) {
                throw new ValidationException("This clan name is already taken");
            }
            String query = "INSERT INTO clans(name, gold) VALUES (?,?)";
            ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();
            qr.insert(conn, query, scalarHandler, clanDto.getName(), clanDto.getGold());
        }
    }

    @Override
    public Clan update(Clan clanDto) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            Clan clan = getClan(clanDto.getId());
            if (clan == null) {
                throw new ValidationException("Wrong clan id");
            }
            if (clanDto.getName() != null) clan.setName(clanDto.getName());
            if (clanDto.getGold() != null) clan.setGold(clanDto.getGold());
            String query = "UPDATE clans SET name = ?, gold = ? WHERE id = ?";
            qr.update(conn, query, clan.getName(), clan.getGold(), clan.getId());
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
    public Clan getClan(Long id) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            String query = "SELECT * FROM CLANS WHERE ID = ?";
            BeanHandler<Clan> beanHandler = new BeanHandler<>(Clan.class);
            return qr.query(conn, query, beanHandler, id);
        }
    }

    @Override
    public List<Clan> getAll() throws SQLException {
        try (Connection conn = SqlSetup.createConnection()){
            String query = "SELECT * FROM CLANS";
            BeanListHandler<Clan> beanListHandler = new BeanListHandler<>(Clan.class);
            return qr.query(conn, query, beanListHandler);
        }
    }
}
