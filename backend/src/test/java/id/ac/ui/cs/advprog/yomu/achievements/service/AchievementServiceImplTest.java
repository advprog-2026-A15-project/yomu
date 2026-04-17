package id.ac.ui.cs.advprog.yomu.achievements.service;

import id.ac.ui.cs.advprog.yomu.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomu.achievements.dto.CreateAchievementRequest;
import id.ac.ui.cs.advprog.yomu.achievements.dto.DailyMissionProgressResponse;
import id.ac.ui.cs.advprog.yomu.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomu.achievements.model.UserCompletedReading;
import id.ac.ui.cs.advprog.yomu.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomu.achievements.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.achievements.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.yomu.achievements.repository.UserCompletedReadingRepository;
import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private DailyMissionRepository dailyMissionRepository;

    @Mock
    private UserCompletedReadingRepository userCompletedReadingRepository;

    @InjectMocks
    private AchievementServiceImpl achievementService;

    private User user;
    private Achievement firstRead;
    private Achievement threeReads;
    private Achievement fiveReads;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("pelajar");
        user.setRole(Role.PELAJAR);

        firstRead = createAchievement(1L, "First Read", 1);
        threeReads = createAchievement(2L, "Reader 3", 3);
        fiveReads = createAchievement(3L, "Reader 5", 5);
    }

    @Test
    void createAchievementShouldTrimValueAndSave() {
        CreateAchievementRequest request = new CreateAchievementRequest();
        request.setName("  Marathon Reader  ");
        request.setDescription("  Selesaikan 10 bacaan  ");
        request.setMilestone(10);

        when(achievementRepository.findByName("Marathon Reader")).thenReturn(Optional.empty());
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Achievement result = achievementService.createAchievement(request);

        assertEquals("Marathon Reader", result.getName());
        assertEquals("Selesaikan 10 bacaan", result.getDescription());
        assertEquals(10, result.getMilestone());
    }

    @Test
    void createAchievementShouldRejectDuplicateName() {
        CreateAchievementRequest request = new CreateAchievementRequest();
        request.setName("First Read");
        request.setMilestone(1);

        when(achievementRepository.findByName("First Read")).thenReturn(Optional.of(firstRead));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> achievementService.createAchievement(request)
        );

        assertEquals(BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void recordCompletedReadingShouldSaveProgressAndUnlockEligibleAchievements() {
        UUID bacaanId = UUID.randomUUID();

        when(userCompletedReadingRepository.existsByUserAndBacaanId(user, bacaanId)).thenReturn(false);
        when(userCompletedReadingRepository.countByUser(user)).thenReturn(3L);
        when(achievementRepository.findAllByOrderByMilestoneAscNameAsc()).thenReturn(List.of(firstRead, threeReads, fiveReads));
        when(userAchievementRepository.findAllByUserOrderByAchievedAtDesc(user)).thenReturn(List.of());
        when(userAchievementRepository.save(any(UserAchievement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        achievementService.recordCompletedReading(user, bacaanId);

        verify(userCompletedReadingRepository).save(any(UserCompletedReading.class));

        ArgumentCaptor<UserAchievement> captor = ArgumentCaptor.forClass(UserAchievement.class);
        verify(userAchievementRepository, times(2)).save(captor.capture());
        List<UserAchievement> savedAchievements = captor.getAllValues();

        assertEquals("First Read", savedAchievements.get(0).getAchievement().getName());
        assertEquals("Reader 3", savedAchievements.get(1).getAchievement().getName());
    }

    @Test
    void recordCompletedReadingShouldIgnoreDuplicateSubmission() {
        UUID bacaanId = UUID.randomUUID();
        when(userCompletedReadingRepository.existsByUserAndBacaanId(user, bacaanId)).thenReturn(true);

        achievementService.recordCompletedReading(user, bacaanId);

        verify(userCompletedReadingRepository, never()).save(any(UserCompletedReading.class));
        verify(userCompletedReadingRepository, never()).countByUser(user);
        verify(userAchievementRepository, never()).save(any(UserAchievement.class));
    }

    @Test
    void getAchievementProgressShouldReturnAllAchievementsWithStatus() {
        UserAchievement unlockedAchievement = new UserAchievement();
        unlockedAchievement.setUser(user);
        unlockedAchievement.setAchievement(firstRead);
        unlockedAchievement.setAchievedAt(LocalDateTime.of(2026, 4, 15, 10, 0));

        when(userCompletedReadingRepository.countByUser(user)).thenReturn(3L);
        when(achievementRepository.findAllByOrderByMilestoneAscNameAsc()).thenReturn(List.of(firstRead, fiveReads));
        when(userAchievementRepository.findAllByUserOrderByAchievedAtDesc(user)).thenReturn(List.of(unlockedAchievement));

        List<AchievementProgressResponse> result = achievementService.getAchievementProgress(user);

        assertEquals(2, result.size());
        assertTrue(result.get(0).isUnlocked());
        assertEquals(1, result.get(0).getCurrentProgress());
        assertEquals(unlockedAchievement.getAchievedAt(), result.get(0).getAchievedAt());

        assertFalse(result.get(1).isUnlocked());
        assertEquals(3, result.get(1).getCurrentProgress());
        assertEquals(5, result.get(1).getMilestone());
        verify(userAchievementRepository, never()).save(any(UserAchievement.class));
    }

    @Test
    void getActiveDailyMissionProgressShouldUseTodayReadingCount() {
        LocalDate today = LocalDate.now();
        DailyMission mission = new DailyMission();
        mission.setId(1L);
        mission.setName("Baca 2 Bacaan");
        mission.setDescription("Selesaikan dua bacaan hari ini");
        mission.setMilestone(2);
        mission.setMissionDate(today);

        when(userCompletedReadingRepository.countByUserAndCompletedAtGreaterThanEqualAndCompletedAtLessThan(
                eq(user),
                eq(today.atStartOfDay()),
                eq(today.plusDays(1).atStartOfDay())
        )).thenReturn(2L);
        when(dailyMissionRepository.findAllByMissionDateOrderByCreatedAtDesc(today)).thenReturn(List.of(mission));

        List<DailyMissionProgressResponse> result = achievementService.getActiveDailyMissionProgress(user);

        assertEquals(1, result.size());
        assertEquals("Baca 2 Bacaan", result.get(0).getName());
        assertEquals(2, result.get(0).getCurrentProgress());
        assertTrue(result.get(0).isCompleted());
        assertNotNull(result.get(0).getMissionDate());
    }

    private Achievement createAchievement(Long id, String name, Integer milestone) {
        Achievement achievement = new Achievement();
        achievement.setId(id);
        achievement.setName(name);
        achievement.setDescription(name + " description");
        achievement.setMilestone(milestone);
        return achievement;
    }
}
