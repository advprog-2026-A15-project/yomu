package id.ac.ui.cs.advprog.yomu.achievements.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAchievementRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(1)
    private Integer milestone;
}
