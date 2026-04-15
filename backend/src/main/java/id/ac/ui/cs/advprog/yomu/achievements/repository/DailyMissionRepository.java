package id.ac.ui.cs.advprog.yomu.achievements.repository;

import id.ac.ui.cs.advprog.yomu.achievements.model.DailyMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    List<DailyMission> findAllByMissionDateOrderByCreatedAtDesc(LocalDate missionDate);
    List<DailyMission> findAllByOrderByMissionDateDescCreatedAtDesc();
}
