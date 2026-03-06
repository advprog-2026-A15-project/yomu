package id.ac.ui.cs.advprog.yomu.league.repository;

import id.ac.ui.cs.advprog.yomu.league.models.Clan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClanRepository extends JpaRepository<Clan, Long> {
    boolean existsByName(String name);
    Optional<Clan> findByName(String name);
}

