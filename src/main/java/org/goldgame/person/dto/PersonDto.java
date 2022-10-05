package org.goldgame.person.dto;

import lombok.*;
import org.goldgame.utilities.Create;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class PersonDto {
    @NotNull(groups = Create.class)
    @NotEmpty(groups = Create.class)
    String name;
    Long gold;
    Long clanId;
    String clanName;
}
