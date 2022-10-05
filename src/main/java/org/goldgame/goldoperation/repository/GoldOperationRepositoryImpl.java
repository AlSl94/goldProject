package org.goldgame.goldoperation.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.goldgame.clan.dto.ClanDto;
import org.goldgame.clan.model.Clan;
import org.goldgame.exception.ValidationException;
import org.goldgame.exception.WrongParameterException;
import org.goldgame.goldoperation.model.GoldOperation;
import org.goldgame.person.dto.PersonDto;
import org.goldgame.utilities.SqlSetup;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class GoldOperationRepositoryImpl implements GoldOperationRepository {

    private final QueryRunner qr = new QueryRunner();

    @Override
    public void goldOperation(Long clanId, Long personId, Long amount, Boolean isWithdraw) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {

            goldOperationValidation(conn, clanId, personId);

            String clanGoldQuery = "SELECT c.gold FROM clans c WHERE c.id = ?"; // Проверка, занято ли имя
            Long clanGold = qr.query(conn, clanGoldQuery, new BeanHandler<>(ClanDto.class), clanId).getGold();

            String personGoldQuery = "SELECT p.gold FROM PERSONS p WHERE p.id = ?"; // Проверка, занято ли имя
            Long personGold = qr.query(conn, personGoldQuery, new BeanHandler<>(ClanDto.class), clanId).getGold();

            if (Boolean.TRUE.equals(isWithdraw) && (clanGold < amount)) {
                throw new ValidationException("Trying to get more gold than in clan's gold repository");
            }

            if (Boolean.TRUE.equals(!isWithdraw) && (personGold < amount)) {
                throw new ValidationException("Trying to store more gold than in person's gold repository");
            }

            String clanQuery = "UPDATE clans SET gold = ? WHERE id = ?";
            String personQuery = "UPDATE persons SET gold = ? WHERE id = ?";
            if (Boolean.TRUE.equals(isWithdraw)) {
                qr.update(conn, clanQuery, clanGold - amount, clanId);
                qr.update(conn, personQuery, personGold + amount, personId);
            } else {
                qr.update(conn, clanQuery, clanGold + amount, clanId);
                qr.update(conn, personQuery, personGold - amount, personId);
            }

            String query = "INSERT INTO GOLD_OPERATIONS (CLAN_ID, PERSON_ID, AMOUNT, OPERATION_TIME, " +
                    "INITIAL_CLAN_GOLD_AMOUNT, IS_WITHDRAWAL) VALUES (?, ?, ?, ?, ?, ?);";
            ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();
            qr.insert(conn, query, scalarHandler, clanId, personId, amount, LocalDateTime.now(),
                    clanGold, isWithdraw);
        }
    }

    @Override
    public GoldOperation getOperation(Long id) throws SQLException {
        try (Connection conn = SqlSetup.createConnection()) {
            GoldOperation goldOperation = null;
            String query = "SELECT " +
                    "go.OPERATION_ID as operationId, " +
                    "go.CLAN_ID as clanId, " +
                    "go.PERSON_ID as personId, " +
                    "go.AMOUNT as amount, " +
                    "go.OPERATION_TIME as operationTime, " +
                    "go.INITIAL_CLAN_GOLD_AMOUNT as initialGold, " +
                    "go.IS_WITHDRAWAL as isWithdrawal " +
                    "FROM GOLD_OPERATIONS go WHERE go.OPERATION_ID = ?";
            try {
                goldOperation = qr.query(conn, query, new BeanHandler<>(GoldOperation.class), id);
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
            return goldOperation;
        }
    }

    private void goldOperationValidation(Connection conn, Long clanId, Long personId) throws SQLException {

        String checkClanQuery = "SELECT c.id, c.NAME, c.gold FROM clans c WHERE c.id = ?";
        BeanHandler<Clan> clanHandler = new BeanHandler<>(Clan.class);
        Clan clan = qr.query(conn, checkClanQuery, clanHandler, clanId);
        if (clan == null) {
            throw new WrongParameterException("Wrong clan id");
        }

        String checkPersonQuery = "SELECT p.id, p.name, p.gold, cp.CLAN_ID as clanId FROM persons p " +
                "JOIN CLAN_PERSON cp ON cp.person_id = p.ID " +
                "WHERE p.id = ?";
        BeanHandler<PersonDto> personHandler = new BeanHandler<>(PersonDto.class);
        PersonDto person = qr.query(conn, checkPersonQuery, personHandler, personId);
        if (person == null) {
            throw new WrongParameterException("Wrong person id");
        }

        if (!Objects.equals(person.getClanId(), clanId)) {
            throw new WrongParameterException("Person clan id is " + person.getClanId() + ", should be " + clanId);
        }
    }
}
