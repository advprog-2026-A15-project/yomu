package id.ac.ui.cs.advprog.yomu.achievements.model;

import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Achievement achievement;

    private LocalDateTime achievedAt = LocalDateTime.now();
}