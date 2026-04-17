package id.ac.ui.cs.advprog.yomu.clan.service;

import id.ac.ui.cs.advprog.yomu.achievements.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.clan.model.Clan;
import id.ac.ui.cs.advprog.yomu.clan.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanScoreServiceImplTest {

    @Mock
    private ClanRepository clanRepository;
    @Mock
    private ClanMemberRepository clanMemberRepository;
    @Mock
    private UserAchievementRepository userAchievementRepository;

    @InjectMocks
    private ClanScoreServiceImpl clanScoreService;

    @Test
    void calculateClanScore_sumsMemberAchievementPoints() {
        Clan clan = new Clan();
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        ClanMember member1 = new ClanMember();
        member1.setUser(user1);
        ClanMember member2 = new ClanMember();
        member2.setUser(user2);

        when(clanMemberRepository.findAllByClan(clan)).thenReturn(List.of(member1, member2));
        when(userAchievementRepository.countByUser(user1)).thenReturn(2L);
        when(userAchievementRepository.countByUser(user2)).thenReturn(1L);

        long score = clanScoreService.calculateClanScore(clan);

        assertEquals(30L, score);
    }

    @Test
    void refreshAllClanScores_updatesEveryClanAndPersists() {
        Clan clan1 = new Clan();
        clan1.setId(1L);
        Clan clan2 = new Clan();
        clan2.setId(2L);

        User user = new User();
        ClanMember member = new ClanMember();
        member.setUser(user);

        when(clanRepository.findAll()).thenReturn(List.of(clan1, clan2));
        when(clanMemberRepository.findAllByClan(any(Clan.class))).thenReturn(List.of(member));
        when(userAchievementRepository.countByUser(user)).thenReturn(1L);

        clanScoreService.refreshAllClanScores();

        assertEquals(10L, clan1.getCurrentSeasonPoints());
        assertEquals(10L, clan2.getCurrentSeasonPoints());
        verify(clanRepository, times(1)).saveAll(anyList());
    }
}
