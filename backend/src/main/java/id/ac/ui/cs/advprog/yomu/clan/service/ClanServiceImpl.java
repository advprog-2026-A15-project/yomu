package id.ac.ui.cs.advprog.yomu.clan.service;

import id.ac.ui.cs.advprog.yomu.authentication.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.clan.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomu.clan.model.Clan;
import id.ac.ui.cs.advprog.yomu.clan.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.clan.repository.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClanServiceImpl implements ClanService {

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClanResponse> getAllClans(User currentUser) {
        return clanRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(clan -> toResponse(clan, currentUser))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClanResponse> getMyClans(User currentUser) {
        return clanMemberRepository.findAllByUser(currentUser).stream()
                .map(ClanMember::getClan)
                .map(clan -> toResponse(clan, currentUser))
                .toList();
    }

    @Override
    @Transactional
    public ClanResponse createClan(CreateClanRequest request, User currentUser) {
        assertPelajar(currentUser);
        String clanName = request.getName().trim();

        if (clanRepository.existsByNameIgnoreCase(clanName)) {
            throw new RuntimeException("Nama clan sudah dipakai");
        }

        Clan clan = new Clan();
        clan.setName(clanName);
        clan.setDescription(request.getDescription());
        clan.setOwner(currentUser);
        clan = clanRepository.save(clan);

        ClanMember ownerMembership = new ClanMember();
        ownerMembership.setClan(clan);
        ownerMembership.setUser(currentUser);
        clanMemberRepository.save(ownerMembership);

        return toResponse(clan, currentUser);
    }

    @Override
    @Transactional
    public ClanResponse joinClan(Long clanId, User currentUser) {
        assertPelajar(currentUser);

        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new ResourceNotFoundException("Clan tidak ditemukan"));

        if (!clanMemberRepository.existsByClanAndUser(clan, currentUser)) {
            ClanMember clanMember = new ClanMember();
            clanMember.setClan(clan);
            clanMember.setUser(currentUser);
            clanMemberRepository.save(clanMember);
        }

        return toResponse(clan, currentUser);
    }

    private ClanResponse toResponse(Clan clan, User currentUser) {
        long memberCount = clanMemberRepository.countByClan(clan);
        boolean joined = clanMemberRepository.existsByClanAndUser(clan, currentUser);
        return new ClanResponse(
                clan.getId(),
                clan.getName(),
                clan.getDescription(),
                clan.getOwner().getUsername(),
                memberCount,
                joined,
                clan.getCreatedAt()
        );
    }

    private void assertPelajar(User currentUser) {
        if (currentUser.getRole() != Role.PELAJAR) {
            throw new RuntimeException("Hanya pelajar yang dapat mengelola clan");
        }
    }
}
