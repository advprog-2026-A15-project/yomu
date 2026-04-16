package id.ac.ui.cs.advprog.yomu.learning.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "bacaan_id", nullable = false)
    @JsonBackReference // Pasangan dari JsonManagedReference di atas
    private Bacaan bacaan;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String pertanyaan;

    @Column(nullable = false)
    private String jawabanBenar;
}