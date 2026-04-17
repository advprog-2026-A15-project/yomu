package id.ac.ui.cs.advprog.yomu.clan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "league_season_state")
@Getter
@Setter
public class LeagueSeasonState {
    @Id
    private Long id;

    @Column(name = "season_number", nullable = false)
    private int seasonNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeasonStatus status;

    @Column(name = "last_processed_at")
    private LocalDateTime lastProcessedAt;
}
