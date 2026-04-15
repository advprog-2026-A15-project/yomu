package id.ac.ui.cs.advprog.yomu.achievements.controller;

import id.ac.ui.cs.advprog.yomu.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomu.achievements.dto.DailyMissionProgressResponse;
import id.ac.ui.cs.advprog.yomu.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public List<AchievementProgressResponse> getMyAchievements() {
        return achievementService.getAchievementProgress(getAuthenticatedUser());
    }

    @GetMapping("/daily-missions/active")
    public List<DailyMissionProgressResponse> getActiveDailyMissions() {
        return achievementService.getActiveDailyMissionProgress(getAuthenticatedUser());
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    }
}
