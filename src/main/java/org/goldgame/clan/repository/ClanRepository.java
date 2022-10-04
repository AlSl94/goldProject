package org.goldgame.clan.repository;


import org.goldgame.clan.dto.ClanDto;

import java.sql.SQLException;
import java.util.List;

public interface ClanRepository {

    void create(ClanDto clanDto) throws SQLException;

    ClanDto update(Long id, ClanDto clanDto) throws SQLException;

    void delete(Long id) throws SQLException;

    ClanDto getClan(Long id) throws SQLException;

    List<ClanDto> getAll() throws SQLException;
}