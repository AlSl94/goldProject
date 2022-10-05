package org.goldgame.goldoperation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldgame.goldoperation.model.GoldOperation;
import org.goldgame.goldoperation.repository.GoldOperationRepository;
import org.goldgame.utilities.SqlSetup;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class GoldOperationService {

    private final GoldOperationRepository goldOperationRepository;

    public void goldOperation(Long clanId, Long personId, Long amount, Boolean isWithdrawal) {
        try {
            goldOperationRepository.goldOperation(clanId, personId, amount, isWithdrawal);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
    }

    public GoldOperation getOperation(Long id) {
        GoldOperation goldOperation = null;
        try {
            goldOperation = goldOperationRepository.getOperation(id);
        } catch (SQLException e) {
            SqlSetup.logSqlError(e);
        }
        return goldOperation;
    }
}
