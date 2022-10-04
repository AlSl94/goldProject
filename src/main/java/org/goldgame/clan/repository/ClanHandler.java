package org.goldgame.clan.repository;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.goldgame.clan.model.Clan;
import org.goldgame.person.model.Person;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClanHandler extends BeanListHandler<Clan> {

    private Connection connection;
    public ClanHandler(Connection con) {
        super(Clan.class);
        this.connection = con;
    }

    @Override
    public List<Clan> handle(ResultSet rs) throws SQLException {
        List<Clan> clans = super.handle(rs);
        QueryRunner runner = new QueryRunner();

        BeanListHandler<Person> handler = new BeanListHandler<>(Person.class);

        String query = "SELECT * FROM persons p WHERE p.ID = ?";

        for (Clan clan : clans) {
            List<Person> persons
                    = runner.query(connection, query, handler, clan.getId());
            clan.setPopulation(persons);
        }
        return clans;
    }
}
