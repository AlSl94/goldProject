package org.goldgame.goldoperation.repository;

import org.goldgame.goldoperation.model.GoldOperation;

import java.sql.SQLException;

public interface GoldOperationRepository {

    void goldOperation(Long clanId, Long personId, Long amount, Boolean isWithdraw) throws SQLException;

    GoldOperation getOperation(Long id) throws SQLException;
}
