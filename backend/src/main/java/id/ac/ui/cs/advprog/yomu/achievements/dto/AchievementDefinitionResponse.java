package id.ac.ui.cs.advprog.yomu.achievements.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AchievementDefinitionResponse {
    private Long id;
    private String name;
    private String description;
    private Integer milestone;
}
