package org.goldgame.clan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.clan.dto.ClanDto;
import org.goldgame.exception.ValidationException;
import org.goldgame.clan.repository.ClanRepository;
import org.goldgame.utilities.SqlSetup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ClanService {

    private final ClanRepository clanRepository;

    public void create(ClanDto clanDto) {
        if (clanDto.getName() == null) {
            throw new ValidationException("Name can't be empty");
        }
        if (clanDto.getGold() == null) clanDto.setGold(0L);

        try {
            clanRepository.create(clanDto);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
    }

    public ClanDto update(Long id, ClanDto clanDto) {
        ClanDto clan = null;
        try {
           clan = clanRepository.update(id, clanDto);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return clan;
    }

    public void delete(Long id) {
        try {
            clanRepository.delete(id);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
    }

    public ClanDto getClan(Long id) {
        ClanDto clan = null;
        try {
            clan = clanRepository.getClan(id);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return clan;
    }

    public List<ClanDto> getAll() {
        List<ClanDto> clans = new ArrayList<>();
        try {
            clans = clanRepository.getAll();
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return clans;
    }
}
