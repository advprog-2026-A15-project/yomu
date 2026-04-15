package id.ac.ui.cs.advprog.yomu.achievements.service;

import id.ac.ui.cs.advprog.yomu.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomu.achievements.dto.CreateAchievementRequest;
import id.ac.ui.cs.advprog.yomu.achievements.dto.CreateDailyMissionRequest;
import id.ac.ui.cs.advprog.yomu.achievements.dto.DailyMissionProgressResponse;
import id.ac.ui.cs.advprog.yomu.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;

import java.util.List;
import java.util.UUID;

public interface AchievementService {

    Achievement createAchievement(CreateAchievementRequest request);
    List<Achievement> getAllAchievements();
    DailyMission createDailyMission(CreateDailyMissionRequest request);
    List<DailyMission> getAllDailyMissions();
    void recordCompletedReading(User user, UUID bacaanId);
    List<AchievementProgressResponse> getAchievementProgress(User user);
    List<DailyMissionProgressResponse> getActiveDailyMissionProgress(User user);
}
