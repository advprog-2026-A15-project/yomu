package id.ac.ui.cs.advprog.yomu.league.dto;

import id.ac.ui.cs.advprog.yomu.league.models.Clan;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClanResponse {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Double totalScore;
    private int memberCount;
    private LocalDateTime createdAt;

    public static ClanResponse from(Clan clan) {
        ClanResponse res = new ClanResponse();
        res.setId(clan.getId());
        res.setName(clan.getName());
        res.setDescription(clan.getDescription());
        res.setOwnerId(clan.getOwnerId());
        res.setTotalScore(clan.getTotalScore());
        res.setMemberCount(clan.getMembers().size());
        res.setCreatedAt(clan.getCreatedAt());
        return res;
    }
}