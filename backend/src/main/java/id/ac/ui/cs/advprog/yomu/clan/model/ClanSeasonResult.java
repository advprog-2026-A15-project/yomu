package id.ac.ui.cs.advprog.yomu.clan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clan_season_result")
@Getter
@Setter
public class ClanSeasonResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season_number", nullable = false)
    private int seasonNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    private Clan clan;

    @Enumerated(EnumType.STRING)
    @Column(name = "league_before", nullable = false, length = 20)
    private LeagueTier leagueBefore;

    @Enumerated(EnumType.STRING)
    @Column(name = "league_after", nullable = false, length = 20)
    private LeagueTier leagueAfter;

    @Column(name = "rank_in_league", nullable = false)
    private int rankInLeague;

    @Column(nullable = false)
    private long points;

    @Column(nullable = false)
    private boolean promoted;

    @Column(nullable = false)
    private boolean relegated;

    @Column(name = "season_winner", nullable = false)
    private boolean seasonWinner;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @PrePersist
    void onCreate() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }
}
