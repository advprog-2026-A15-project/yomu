package id.ac.ui.cs.advprog.yomu.achievements.repository;

import id.ac.ui.cs.advprog.yomu.achievements.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    Optional<Achievement> findByName(String name);

}