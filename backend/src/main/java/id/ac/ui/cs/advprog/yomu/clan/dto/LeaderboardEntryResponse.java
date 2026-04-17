package id.ac.ui.cs.advprog.yomu.clan.dto;

import id.ac.ui.cs.advprog.yomu.clan.model.LeagueTier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private Long clanId;
    private String clanName;
    private LeagueTier league;
    private long points;
    private int rank;
}
