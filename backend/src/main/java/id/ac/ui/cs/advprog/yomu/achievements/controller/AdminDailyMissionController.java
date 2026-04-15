package id.ac.ui.cs.advprog.yomu.achievements.controller;

import id.ac.ui.cs.advprog.yomu.achievements.dto.CreateDailyMissionRequest;
import id.ac.ui.cs.advprog.yomu.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomu.achievements.model.DailyMission;
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
@RequestMapping("/api/admin/daily-missions")
@RequiredArgsConstructor
public class AdminDailyMissionController {

    private final AchievementService achievementService;

    @GetMapping
    public List<DailyMissionResponse> getDailyMissions() {
        return achievementService.getAllDailyMissions().stream()
                .map(this::toDailyMissionResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<DailyMissionResponse> createDailyMission(
            @Valid @RequestBody CreateDailyMissionRequest request
    ) {
        DailyMission dailyMission = achievementService.createDailyMission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDailyMissionResponse(dailyMission));
    }

    private DailyMissionResponse toDailyMissionResponse(DailyMission dailyMission) {
        return new DailyMissionResponse(
                dailyMission.getId(),
                dailyMission.getName(),
                dailyMission.getDescription(),
                dailyMission.getMilestone(),
                dailyMission.getMissionDate()
        );
    }
}
