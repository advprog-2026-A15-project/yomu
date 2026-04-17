package id.ac.ui.cs.advprog.yomu.clan.repository;

import id.ac.ui.cs.advprog.yomu.clan.model.Clan;
import id.ac.ui.cs.advprog.yomu.clan.model.LeagueTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClanRepository extends JpaRepository<Clan, Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Clan> findAllByCurrentLeagueOrderByCurrentSeasonPointsDescCreatedAtAsc(LeagueTier league);
}
