package org.goldgame.clan.repository;


import org.goldgame.clan.model.Clan;

import java.sql.SQLException;
import java.util.List;

public interface ClanRepository {

    void create(Clan clanDto) throws SQLException;

    Clan update(Clan clanDto) throws SQLException;

    void delete(Long id) throws SQLException;

    Clan getClan(Long id) throws SQLException;

    List<Clan> getAll() throws SQLException;
}