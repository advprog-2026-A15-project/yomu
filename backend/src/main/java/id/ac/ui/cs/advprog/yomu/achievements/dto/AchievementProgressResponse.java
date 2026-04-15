package id.ac.ui.cs.advprog.yomu.achievements.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AchievementProgressResponse {
    private Long achievementId;
    private String name;
    private String description;
    private Integer milestone;
    private Integer currentProgress;
    private boolean unlocked;
    private LocalDateTime achievedAt;
}
