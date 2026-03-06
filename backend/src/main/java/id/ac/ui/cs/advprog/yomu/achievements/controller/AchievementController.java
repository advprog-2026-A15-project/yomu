package id.ac.ui.cs.advprog.yomu.achievements.controller;

import id.ac.ui.cs.advprog.yomu.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomu.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public List<UserAchievementResponse> getMyAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        return achievementService.getUserAchievements(user).stream()
                .map(ua -> new UserAchievementResponse(
                        ua.getAchievement().getId(),
                        ua.getAchievement().getName(),
                        ua.getAchievement().getDescription(),
                        ua.getAchievedAt()
                ))
                .toList();
    }
}
