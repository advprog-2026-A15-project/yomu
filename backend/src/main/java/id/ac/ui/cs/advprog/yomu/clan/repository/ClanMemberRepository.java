package id.ac.ui.cs.advprog.yomu.clan.repository;

import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.clan.model.Clan;
import id.ac.ui.cs.advprog.yomu.clan.model.ClanMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClanMemberRepository extends JpaRepository<ClanMember, Long> {
    boolean existsByClanAndUser(Clan clan, User user);
    long countByClan(Clan clan);
    List<ClanMember> findAllByUser(User user);
}
