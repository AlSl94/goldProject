package org.goldgame.goldoperation.model;

import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
public class GoldOperation {
    Long operationId;
    Long clanId;
    Long personId;
    Long amount;
    Timestamp operationTime;
    Long initialGold;
    Boolean isWithdrawal;
}
