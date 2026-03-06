package id.ac.ui.cs.advprog.yomu.league.service;

import id.ac.ui.cs.advprog.yomu.league.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomu.league.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.league.models.Clan;
import id.ac.ui.cs.advprog.yomu.league.models.ClanMember;
import id.ac.ui.cs.advprog.yomu.league.models.ClanRole;
import id.ac.ui.cs.advprog.yomu.league.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.league.repository.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClanService {

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;

    @Transactional
    public ClanResponse createClan(CreateClanRequest request, Long userId) {
        if (clanRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Clan name already taken: " + request.getName());
        }

        if (clanMemberRepository.existsByUserId(userId)) {
            throw new IllegalStateException("User is already a member of a clan.");
        }

        Clan clan = new Clan();
        clan.setName(request.getName());
        clan.setDescription(request.getDescription());
        clan.setOwnerId(userId);
        clan = clanRepository.save(clan);

        // Creator of the clan automatically become the leader
        ClanMember leader = new ClanMember();
        leader.setClan(clan);
        leader.setUserId(userId);
        leader.setRole(ClanRole.LEADER);
        clanMemberRepository.save(leader);

        return ClanResponse.from(clan);
    }
}