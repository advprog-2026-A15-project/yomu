package id.ac.ui.cs.advprog.yomu.forum.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String isiKomentar;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bacaan_id", nullable = false)
    private Bacaan bacaan;

    @Column(name = "bacaan_id", insertable = false, updatable = false)
    private UUID bacaanId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private UUID parentId;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();
}