package id.ac.ui.cs.advprog.yomu.achievements.service;

import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.achievements.model.UserAchievement;

import java.util.List;

public interface AchievementService {

    void unlockFirstReadAchievement(User user);
    List<UserAchievement> getUserAchievements(User user);

}
