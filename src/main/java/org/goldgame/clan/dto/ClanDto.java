package org.goldgame.clan.dto;

import lombok.*;
import org.goldgame.person.dto.PersonDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter @Setter @ToString
public class ClanDto {
    private String name;
    private Long gold;
    private List<PersonDto> population;
}
