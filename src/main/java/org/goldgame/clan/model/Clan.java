package org.goldgame.clan.model;

import lombok.*;
import org.goldgame.person.model.Person;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
public class Clan {
    private Long id;
    private String name;
    private List<Person> population;
    private Long gold;
}