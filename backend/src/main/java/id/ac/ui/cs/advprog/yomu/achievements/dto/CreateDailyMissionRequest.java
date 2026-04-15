package id.ac.ui.cs.advprog.yomu.achievements.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDailyMissionRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(1)
    private Integer milestone;

    private LocalDate missionDate;
}
