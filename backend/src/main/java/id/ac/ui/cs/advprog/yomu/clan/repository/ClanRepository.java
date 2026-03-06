package id.ac.ui.cs.advprog.yomu.clan.repository;

import id.ac.ui.cs.advprog.yomu.clan.model.Clan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClanRepository extends JpaRepository<Clan, Long> {
    boolean existsByNameIgnoreCase(String name);
}
