package id.ac.ui.cs.advprog.yomu.achievements.service;

import id.ac.ui.cs.advprog.yomu.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomu.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomu.achievements.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    @Override
    public void unlockFirstReadAchievement(User user) {

        Achievement achievement = achievementRepository
                .findByName("First Read")
                .orElseThrow(() -> new RuntimeException("Achievement tidak ditemukan"));

        if (!userAchievementRepository.existsByUserAndAchievement(user, achievement)) {

            UserAchievement ua = new UserAchievement();
            ua.setUser(user);
            ua.setAchievement(achievement);
            ua.setAchievedAt(LocalDateTime.now());

            userAchievementRepository.save(ua);
        }
    }

    @Override
    public List<UserAchievement> getUserAchievements(User user) {
        return userAchievementRepository.findAllByUserOrderByAchievedAtDesc(user);
    }
}
