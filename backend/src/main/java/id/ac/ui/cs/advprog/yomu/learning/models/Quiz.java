package id.ac.ui.cs.advprog.yomu.learning.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter @Setter
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    // Menyambungkan kuis ke bacaan tertentu
    @ManyToOne
    @JoinColumn(name = "bacaan_id", nullable = false)
    private Bacaan bacaan;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String pertanyaan;

    @Column(nullable = false)
    private String jawabanBenar;
}