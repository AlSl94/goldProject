package org.goldgame.clan;

import org.goldgame.clan.dto.ClanDto;
import org.goldgame.clan.model.Clan;

import java.util.ArrayList;
import java.util.List;

public class ClanMapper {

    private ClanMapper() {
        throw new IllegalStateException("Utility class");
    }
    public static ClanDto toClanDto(Clan clan) {
        return ClanDto.builder()
                .name(clan.getName())
                .gold(clan.getGold())
                .build();
    }

    public static Clan toClan(ClanDto clanDto) {
        return Clan.builder()
                .name(clanDto.getName())
                .gold(clanDto.getGold())
                .build();
    }

    public static List<ClanDto> toClanDtoList(Iterable<Clan> clans) {
        List<ClanDto> result = new ArrayList<>();
        for (Clan clan : clans) {
            result.add(toClanDto(clan));
        }
        return result;
    }
}
