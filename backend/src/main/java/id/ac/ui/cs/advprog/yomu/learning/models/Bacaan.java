package id.ac.ui.cs.advprog.yomu.learning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Bacaan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String judul;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String isiTeks;

    // 👇 Tambahan relasi agar Kuis ikut terkirim ke Frontend 👇
    @OneToMany(mappedBy = "bacaan", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference // Mencegah infinite loop (JSON muter-muter)
    private List<Quiz> quizzes;

    // Relasi ke Comment dengan cascade delete
    @JsonIgnore
    @OneToMany(mappedBy = "bacaan", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments;
}