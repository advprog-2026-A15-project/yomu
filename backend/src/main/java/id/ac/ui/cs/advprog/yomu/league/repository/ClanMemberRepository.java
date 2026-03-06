package id.ac.ui.cs.advprog.yomu.league.repository;

import id.ac.ui.cs.advprog.yomu.league.models.ClanMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClanMemberRepository extends JpaRepository<ClanMember, Long> {
    boolean existsByUserId(Long userId);
    Optional<ClanMember> findByUserId(Long userId);
}

