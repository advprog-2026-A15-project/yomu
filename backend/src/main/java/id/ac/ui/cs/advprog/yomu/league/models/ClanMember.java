package id.ac.ui.cs.advprog.yomu.league.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "clan_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"clan_id", "user_id"}))
@Data
@NoArgsConstructor
public class ClanMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    private Clan clan;

    @Column(name = "user_id", nullable = false)
    private Long userId; // references User.id from Auth module

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ClanRole role = ClanRole.MEMBER;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}