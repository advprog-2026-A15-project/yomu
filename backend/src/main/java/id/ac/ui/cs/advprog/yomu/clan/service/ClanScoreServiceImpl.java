package id.ac.ui.cs.advprog.yomu.clan.service;

import id.ac.ui.cs.advprog.yomu.achievements.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.clan.model.Clan;
import id.ac.ui.cs.advprog.yomu.clan.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClanScoreServiceImpl implements ClanScoreService {

    private static final long ACHIEVEMENT_POINT = 10L;

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;
    private final UserAchievementRepository userAchievementRepository;

    @Override
    @Transactional(readOnly = true)
    public long calculateClanScore(Clan clan) {
        List<ClanMember> members = clanMemberRepository.findAllByClan(clan);
        long totalAchievementPoints = 0L;
        for (ClanMember member : members) {
            User user = member.getUser();
            totalAchievementPoints += userAchievementRepository.countByUser(user) * ACHIEVEMENT_POINT;
        }
        return totalAchievementPoints;
    }

    @Override
    @Transactional
    public void refreshClanScore(Clan clan) {
        clan.setCurrentSeasonPoints(calculateClanScore(clan));
        clanRepository.save(clan);
    }

    @Override
    @Transactional
    public void refreshAllClanScores() {
        List<Clan> clans = clanRepository.findAll();
        for (Clan clan : clans) {
            clan.setCurrentSeasonPoints(calculateClanScore(clan));
        }
        clanRepository.saveAll(clans);
    }
}
