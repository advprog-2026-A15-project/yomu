package id.ac.ui.cs.advprog.yomu.clan.dto;

import id.ac.ui.cs.advprog.yomu.clan.model.LeagueTier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ClanResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerUsername;
    private long memberCount;
    private boolean joined;
    private LeagueTier currentLeague;
    private long currentSeasonPoints;
    private Integer lastSeasonRank;
    private LocalDateTime createdAt;
}
