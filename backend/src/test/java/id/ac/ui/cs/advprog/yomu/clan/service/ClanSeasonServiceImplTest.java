package id.ac.ui.cs.advprog.yomu.clan.service;

import id.ac.ui.cs.advprog.yomu.clan.dto.SeasonEndResponse;
import id.ac.ui.cs.advprog.yomu.clan.model.*;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanSeasonResultRepository;
import id.ac.ui.cs.advprog.yomu.clan.repository.LeagueSeasonStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanSeasonServiceImplTest {

    @Mock
    private ClanRepository clanRepository;
    @Mock
    private ClanSeasonResultRepository clanSeasonResultRepository;
    @Mock
    private LeagueSeasonStateRepository leagueSeasonStateRepository;
    @Mock
    private ClanScoreService clanScoreService;

    @InjectMocks
    private ClanSeasonServiceImpl clanSeasonService;

    @Test
    void endSeason_appliesRuleBTransitionsAndResetsPoints() {
        LeagueSeasonState state = new LeagueSeasonState();
        state.setId(1L);
        state.setSeasonNumber(1);
        state.setStatus(SeasonStatus.ACTIVE);
        when(leagueSeasonStateRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(state));

        List<Clan> bronze = List.of(
                clan(1L, LeagueTier.BRONZE, 100),
                clan(2L, LeagueTier.BRONZE, 90),
                clan(3L, LeagueTier.BRONZE, 80)
        );
        List<Clan> silver = List.of(
                clan(4L, LeagueTier.SILVER, 100),
                clan(5L, LeagueTier.SILVER, 90),
                clan(6L, LeagueTier.SILVER, 80),
                clan(7L, LeagueTier.SILVER, 70),
                clan(8L, LeagueTier.SILVER, 60)
        );
        List<Clan> gold = List.of(
                clan(9L, LeagueTier.GOLD, 120),
                clan(10L, LeagueTier.GOLD, 110),
                clan(11L, LeagueTier.GOLD, 100),
                clan(12L, LeagueTier.GOLD, 90),
                clan(13L, LeagueTier.GOLD, 80)
        );

        when(clanRepository.findAllByCurrentLeagueOrderByCurrentSeasonPointsDescCreatedAtAsc(LeagueTier.BRONZE)).thenReturn(bronze);
        when(clanRepository.findAllByCurrentLeagueOrderByCurrentSeasonPointsDescCreatedAtAsc(LeagueTier.SILVER)).thenReturn(silver);
        when(clanRepository.findAllByCurrentLeagueOrderByCurrentSeasonPointsDescCreatedAtAsc(LeagueTier.GOLD)).thenReturn(gold);

        SeasonEndResponse response = clanSeasonService.endSeason();

        verify(clanScoreService, times(1)).refreshAllClanScores();
        assertEquals(1, response.getCompletedSeasonNumber());
        assertEquals(2, response.getNextSeasonNumber());
        assertEquals(LeagueTier.SILVER, bronze.get(0).getCurrentLeague());
        assertEquals(LeagueTier.SILVER, bronze.get(1).getCurrentLeague());
        assertEquals(LeagueTier.GOLD, silver.get(0).getCurrentLeague());
        assertEquals(LeagueTier.GOLD, silver.get(1).getCurrentLeague());
        assertEquals(LeagueTier.BRONZE, silver.get(4).getCurrentLeague());
        assertEquals(LeagueTier.BRONZE, silver.get(3).getCurrentLeague());
        assertEquals(LeagueTier.SILVER, gold.get(4).getCurrentLeague());
        assertEquals(LeagueTier.SILVER, gold.get(3).getCurrentLeague());
        assertEquals(0L, gold.get(0).getCurrentSeasonPoints());
        assertNotNull(state.getLastProcessedAt());
    }

    @Test
    void endSeason_rejectsWhenAlreadyProcessing() {
        LeagueSeasonState state = new LeagueSeasonState();
        state.setId(1L);
        state.setSeasonNumber(1);
        state.setStatus(SeasonStatus.PROCESSING);
        when(leagueSeasonStateRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(state));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clanSeasonService.endSeason());
        assertEquals("Season sedang diproses", ex.getMessage());

        verify(clanScoreService, never()).refreshAllClanScores();
    }

    @Test
    void endSeason_marksGoldTopOneAsSeasonWinner() {
        LeagueSeasonState state = new LeagueSeasonState();
        state.setId(1L);
        state.setSeasonNumber(3);
        state.setStatus(SeasonStatus.ACTIVE);
        when(leagueSeasonStateRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(state));
        when(clanRepository.findAllByCurrentLeagueOrderByCurrentSeasonPointsDescCreatedAtAsc(LeagueTier.BRONZE)).thenReturn(List.of());
        when(clanRepository.findAllByCurrentLeagueOrderByCurrentSeasonPointsDescCreatedAtAsc(LeagueTier.SILVER)).thenReturn(List.of());
        when(clanRepository.findAllByCurrentLeagueOrderByCurrentSeasonPointsDescCreatedAtAsc(LeagueTier.GOLD)).thenReturn(List.of(
                clan(21L, LeagueTier.GOLD, 50),
                clan(22L, LeagueTier.GOLD, 40),
                clan(23L, LeagueTier.GOLD, 30)
        ));

        clanSeasonService.endSeason();

        ArgumentCaptor<List<ClanSeasonResult>> captor = ArgumentCaptor.forClass(List.class);
        verify(clanSeasonResultRepository, atLeastOnce()).saveAll(captor.capture());
        boolean hasWinner = captor.getAllValues().stream()
                .flatMap(List::stream)
                .anyMatch(result -> result.getLeagueBefore() == LeagueTier.GOLD
                        && result.getRankInLeague() == 1
                        && result.isSeasonWinner());
        assertTrue(hasWinner);
    }

    private Clan clan(Long id, LeagueTier tier, long points) {
        Clan clan = new Clan();
        clan.setId(id);
        clan.setCurrentLeague(tier);
        clan.setCurrentSeasonPoints(points);
        clan.setCreatedAt(LocalDateTime.now());
        return clan;
    }
}
