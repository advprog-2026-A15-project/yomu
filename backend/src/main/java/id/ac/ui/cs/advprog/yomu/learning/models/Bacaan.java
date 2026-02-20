package id.ac.ui.cs.advprog.yomu.learning.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter @Setter
public class Bacaan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String judul;

    @Column(columnDefinition = "TEXT")
    private String isiTeks;
}