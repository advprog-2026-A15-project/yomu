package id.ac.ui.cs.advprog.yomu.clan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeasonEndResponse {
    private int completedSeasonNumber;
    private int nextSeasonNumber;
    private String message;
}
