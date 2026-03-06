package id.ac.ui.cs.advprog.yomu.clan.dto;

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
    private LocalDateTime createdAt;
}
