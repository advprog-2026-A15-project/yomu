package id.ac.ui.cs.advprog.yomu.clan.service;

import id.ac.ui.cs.advprog.yomu.clan.dto.SeasonEndResponse;
import id.ac.ui.cs.advprog.yomu.clan.model.*;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanSeasonResultRepository;
import id.ac.ui.cs.advprog.yomu.clan.repository.LeagueSeasonStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClanSeasonServiceImpl implements ClanSeasonService {

    private static final Long DEFAULT_STATE_ID = 1L;
    private static final int MOVE_COUNT = 2;

    private final ClanRepository clanRepository;
    private final ClanSeasonResultRepository clanSeasonResultRepository;
    private final LeagueSeasonStateRepository leagueSeasonStateRepository;
    private final ClanScoreService clanScoreService;

    @Override
    @Transactional
    public SeasonEndResponse endSeason() {
        LeagueSeasonState state = leagueSeasonStateRepository.findByIdForUpdate(DEFAULT_STATE_ID)
                .orElseGet(this::createDefaultState);
        if (state.getStatus() == SeasonStatus.PROCESSING) {
            throw new RuntimeException("Season sedang diproses");
        }

        state.setStatus(SeasonStatus.PROCESSING);
        leagueSeasonStateRepository.save(state);

        clanScoreService.refreshAllClanScores();
        int completedSeason = state.getSeasonNumber();

        processLeague(LeagueTier.BRONZE, completedSeason);
        processLeague(LeagueTier.SILVER, completedSeason);
        processLeague(LeagueTier.GOLD, completedSeason);

        state.setSeasonNumber(completedSeason + 1);
        state.setStatus(SeasonStatus.ACTIVE);
        state.setLastProcessedAt(LocalDateTime.now());
        leagueSeasonStateRepository.save(state);

        return new SeasonEndResponse(completedSeason, state.getSeasonNumber(), "End season selesai diproses");
    }

    private LeagueSeasonState createDefaultState() {
        LeagueSeasonState state = new LeagueSeasonState();
        state.setId(DEFAULT_STATE_ID);
        state.setSeasonNumber(1);
        state.setStatus(SeasonStatus.ACTIVE);
        return leagueSeasonStateRepository.save(state);
    }

    private void processLeague(LeagueTier league, int seasonNumber) {
        List<Clan> rankedClans = clanRepository.findAllByCurrentLeagueOrderByCurrentSeasonPointsDescCreatedAtAsc(league);
        if (rankedClans.isEmpty()) {
            return;
        }

        Set<Long> promotedIds = new HashSet<>();
        Set<Long> relegatedIds = new HashSet<>();

        if (league == LeagueTier.BRONZE || league == LeagueTier.SILVER) {
            collectTopIds(rankedClans, promotedIds);
        }
        if (league == LeagueTier.SILVER || league == LeagueTier.GOLD) {
            collectBottomIds(rankedClans, relegatedIds, promotedIds);
        }

        List<ClanSeasonResult> seasonResults = new ArrayList<>();
        for (int i = 0; i < rankedClans.size(); i++) {
            Clan clan = rankedClans.get(i);
            int rank = i + 1;
            LeagueTier before = league;
            LeagueTier after = resolveNewLeague(league, clan.getId(), promotedIds, relegatedIds);

            ClanSeasonResult result = new ClanSeasonResult();
            result.setSeasonNumber(seasonNumber);
            result.setClan(clan);
            result.setLeagueBefore(before);
            result.setLeagueAfter(after);
            result.setRankInLeague(rank);
            result.setPoints(clan.getCurrentSeasonPoints());
            result.setPromoted(promotedIds.contains(clan.getId()));
            result.setRelegated(relegatedIds.contains(clan.getId()));
            result.setSeasonWinner(league == LeagueTier.GOLD && rank == 1);
            seasonResults.add(result);

            clan.setCurrentLeague(after);
            clan.setLastSeasonRank(rank);
            clan.setCurrentSeasonPoints(0);
        }

        clanSeasonResultRepository.saveAll(seasonResults);
        clanRepository.saveAll(rankedClans);
    }

    private void collectTopIds(List<Clan> rankedClans, Set<Long> topIds) {
        int count = Math.min(MOVE_COUNT, rankedClans.size());
        for (int i = 0; i < count; i++) {
            topIds.add(rankedClans.get(i).getId());
        }
    }

    private void collectBottomIds(List<Clan> rankedClans, Set<Long> bottomIds, Set<Long> excludedIds) {
        int count = 0;
        for (int i = rankedClans.size() - 1; i >= 0 && count < MOVE_COUNT; i--) {
            Clan clan = rankedClans.get(i);
            if (!excludedIds.contains(clan.getId())) {
                bottomIds.add(clan.getId());
                count++;
            }
        }
    }

    private LeagueTier resolveNewLeague(LeagueTier current, Long clanId, Set<Long> promotedIds, Set<Long> relegatedIds) {
        if (promotedIds.contains(clanId)) {
            if (current == LeagueTier.BRONZE) {
                return LeagueTier.SILVER;
            }
            if (current == LeagueTier.SILVER) {
                return LeagueTier.GOLD;
            }
        }
        if (relegatedIds.contains(clanId)) {
            if (current == LeagueTier.GOLD) {
                return LeagueTier.SILVER;
            }
            if (current == LeagueTier.SILVER) {
                return LeagueTier.BRONZE;
            }
        }
        return current;
    }
}
