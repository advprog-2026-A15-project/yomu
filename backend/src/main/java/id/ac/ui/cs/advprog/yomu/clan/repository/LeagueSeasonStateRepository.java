package id.ac.ui.cs.advprog.yomu.clan.repository;

import id.ac.ui.cs.advprog.yomu.clan.model.LeagueSeasonState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface LeagueSeasonStateRepository extends JpaRepository<LeagueSeasonState, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from LeagueSeasonState s where s.id = :id")
    Optional<LeagueSeasonState> findByIdForUpdate(Long id);
}
