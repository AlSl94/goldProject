package org.goldgame.person;

import org.goldgame.clan.dto.ClanDto;
import org.goldgame.clan.model.Clan;
import org.goldgame.person.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonMapper {

    private PersonMapper() {
        throw new IllegalStateException("Utility class");
    }
    public static ClanDto toPersonDto(Person person) {
        return ClanDto.builder()
                .name(person.getName())
                .gold(person.getGold())
                .build();
    }

    public static Clan toPerson(ClanDto clanDto) {
        return Clan.builder()
                .name(clanDto.getName())
                .gold(clanDto.getGold())
                .build();
    }

    public static List<ClanDto> toPersonDtoList(Iterable<Person> persons) {
        List<ClanDto> result = new ArrayList<>();
        for (Person person : persons) {
            result.add(toPersonDto(person));
        }
        return result;
    }
}
