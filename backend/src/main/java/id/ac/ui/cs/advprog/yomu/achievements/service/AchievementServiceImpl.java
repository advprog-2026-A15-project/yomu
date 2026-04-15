package id.ac.ui.cs.advprog.yomu.achievements.service;

import id.ac.ui.cs.advprog.yomu.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomu.achievements.dto.CreateAchievementRequest;
import id.ac.ui.cs.advprog.yomu.achievements.dto.CreateDailyMissionRequest;
import id.ac.ui.cs.advprog.yomu.achievements.dto.DailyMissionProgressResponse;
import id.ac.ui.cs.advprog.yomu.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomu.achievements.model.UserCompletedReading;
import id.ac.ui.cs.advprog.yomu.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomu.achievements.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.achievements.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.yomu.achievements.repository.UserCompletedReadingRepository;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserCompletedReadingRepository userCompletedReadingRepository;

    @Override
    @Transactional
    public Achievement createAchievement(CreateAchievementRequest request) {
        String achievementName = normalizeRequiredValue(request.getName(), "Nama achievement wajib diisi");
        if (achievementRepository.findByName(achievementName).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "Achievement dengan nama tersebut sudah ada");
        }

        Achievement achievement = new Achievement();
        achievement.setName(achievementName);
        achievement.setDescription(normalizeOptionalValue(request.getDescription()));
        achievement.setMilestone(request.getMilestone());
        return achievementRepository.save(achievement);
    }

    @Override
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAllByOrderByMilestoneAscNameAsc();
    }

    @Override
    @Transactional
    public DailyMission createDailyMission(CreateDailyMissionRequest request) {
        DailyMission dailyMission = new DailyMission();
        dailyMission.setName(normalizeRequiredValue(request.getName(), "Nama daily mission wajib diisi"));
        dailyMission.setDescription(normalizeOptionalValue(request.getDescription()));
        dailyMission.setMilestone(request.getMilestone());
        dailyMission.setMissionDate(request.getMissionDate() != null ? request.getMissionDate() : LocalDate.now());
        return dailyMissionRepository.save(dailyMission);
    }

    @Override
    public List<DailyMission> getAllDailyMissions() {
        return dailyMissionRepository.findAllByOrderByMissionDateDescCreatedAtDesc();
    }

    @Override
    @Transactional
    public void recordCompletedReading(User user, UUID bacaanId) {
        if (userCompletedReadingRepository.existsByUserAndBacaanId(user, bacaanId)) {
            return;
        }

        UserCompletedReading completedReading = new UserCompletedReading();
        completedReading.setUser(user);
        completedReading.setBacaanId(bacaanId);
        completedReading.setCompletedAt(LocalDateTime.now());
        userCompletedReadingRepository.save(completedReading);

        long completedReadingCount = userCompletedReadingRepository.countByUser(user);
        synchronizeAchievements(user, completedReadingCount);
    }

    @Override
    @Transactional
    public List<AchievementProgressResponse> getAchievementProgress(User user) {
        long completedReadingCount = userCompletedReadingRepository.countByUser(user);
        Map<Long, UserAchievement> unlockedAchievements = synchronizeAchievements(user, completedReadingCount);

        return achievementRepository.findAllByOrderByMilestoneAscNameAsc().stream()
                .map(achievement -> toAchievementProgressResponse(
                        achievement,
                        unlockedAchievements.get(achievement.getId()),
                        completedReadingCount
                ))
                .toList();
    }

    @Override
    public List<DailyMissionProgressResponse> getActiveDailyMissionProgress(User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        long todayCompletedReadings = userCompletedReadingRepository
                .countByUserAndCompletedAtGreaterThanEqualAndCompletedAtLessThan(user, startOfDay, endOfDay);

        return dailyMissionRepository.findAllByMissionDateOrderByCreatedAtDesc(today).stream()
                .map(mission -> toDailyMissionProgressResponse(mission, todayCompletedReadings))
                .toList();
    }

    private Map<Long, UserAchievement> synchronizeAchievements(User user, long completedReadingCount) {
        List<Achievement> achievements = achievementRepository.findAllByOrderByMilestoneAscNameAsc();
        Map<Long, UserAchievement> unlockedAchievements = userAchievementRepository.findAllByUserOrderByAchievedAtDesc(user).stream()
                .collect(Collectors.toMap(
                        userAchievement -> userAchievement.getAchievement().getId(),
                        Function.identity(),
                        (existing, ignored) -> existing
                ));

        for (Achievement achievement : achievements) {
            if (completedReadingCount < achievement.getMilestone() || unlockedAchievements.containsKey(achievement.getId())) {
                continue;
            }

            UserAchievement userAchievement = new UserAchievement();
            userAchievement.setUser(user);
            userAchievement.setAchievement(achievement);
            userAchievement.setAchievedAt(LocalDateTime.now());

            UserAchievement savedUserAchievement = userAchievementRepository.save(userAchievement);
            unlockedAchievements.put(achievement.getId(), savedUserAchievement);
        }

        return unlockedAchievements;
    }

    private AchievementProgressResponse toAchievementProgressResponse(
            Achievement achievement,
            UserAchievement userAchievement,
            long completedReadingCount
    ) {
        int cappedProgress = (int) Math.min(completedReadingCount, achievement.getMilestone().longValue());
        return new AchievementProgressResponse(
                achievement.getId(),
                achievement.getName(),
                achievement.getDescription(),
                achievement.getMilestone(),
                cappedProgress,
                userAchievement != null,
                userAchievement != null ? userAchievement.getAchievedAt() : null
        );
    }

    private DailyMissionProgressResponse toDailyMissionProgressResponse(DailyMission mission, long todayCompletedReadings) {
        int cappedProgress = (int) Math.min(todayCompletedReadings, mission.getMilestone().longValue());
        return new DailyMissionProgressResponse(
                mission.getId(),
                mission.getName(),
                mission.getDescription(),
                mission.getMilestone(),
                cappedProgress,
                todayCompletedReadings >= mission.getMilestone(),
                mission.getMissionDate()
        );
    }

    private String normalizeRequiredValue(String value, String message) {
        String normalizedValue = normalizeOptionalValue(value);
        if (normalizedValue == null) {
            throw new ResponseStatusException(BAD_REQUEST, message);
        }
        return normalizedValue;
    }

    private String normalizeOptionalValue(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
