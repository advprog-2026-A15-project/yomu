package id.ac.ui.cs.advprog.yomu.forum.models;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String isiKomentar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bacaan_id", nullable = false)
    private Bacaan bacaan;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}