package id.ac.ui.cs.advprog.yomu.achievements.controller;

import id.ac.ui.cs.advprog.yomu.achievements.dto.AchievementDefinitionResponse;
import id.ac.ui.cs.advprog.yomu.achievements.dto.CreateAchievementRequest;
import id.ac.ui.cs.advprog.yomu.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievements.service.AchievementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/achievements")
@RequiredArgsConstructor
public class AdminAchievementController {

    private final AchievementService achievementService;

    @GetMapping
    public List<AchievementDefinitionResponse> getAchievements() {
        return achievementService.getAllAchievements().stream()
                .map(this::toAchievementDefinitionResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<AchievementDefinitionResponse> createAchievement(
            @Valid @RequestBody CreateAchievementRequest request
    ) {
        Achievement achievement = achievementService.createAchievement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toAchievementDefinitionResponse(achievement));
    }

    private AchievementDefinitionResponse toAchievementDefinitionResponse(Achievement achievement) {
        return new AchievementDefinitionResponse(
                achievement.getId(),
                achievement.getName(),
                achievement.getDescription(),
                achievement.getMilestone()
        );
    }
}
