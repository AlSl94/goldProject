package org.goldgame.clan.dto;

import lombok.*;
import org.goldgame.person.dto.PersonDto;
import org.goldgame.utilities.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
public class ClanDto {
    @NotNull(groups = Create.class)
    @NotBlank(groups = Create.class)
    private String name;
    private Long gold;
    private List<PersonDto> population;
}
