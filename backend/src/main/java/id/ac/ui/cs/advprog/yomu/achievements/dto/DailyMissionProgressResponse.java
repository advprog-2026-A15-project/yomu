package id.ac.ui.cs.advprog.yomu.achievements.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailyMissionProgressResponse {
    private Long dailyMissionId;
    private String name;
    private String description;
    private Integer milestone;
    private Integer currentProgress;
    private boolean completed;
    private LocalDate missionDate;
}
