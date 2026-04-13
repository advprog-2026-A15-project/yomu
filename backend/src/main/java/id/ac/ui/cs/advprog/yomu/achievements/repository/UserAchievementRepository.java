package id.ac.ui.cs.advprog.yomu.achievements.repository;

import id.ac.ui.cs.advprog.yomu.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    boolean existsByUserAndAchievement(User user, Achievement achievement);

    List<UserAchievement> findAllByUserOrderByAchievedAtDesc(User user);

}
