package id.ac.ui.cs.advprog.yomu.achievements.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailyMissionResponse {
    private Long id;
    private String name;
    private String description;
    private Integer milestone;
    private LocalDate missionDate;
}
