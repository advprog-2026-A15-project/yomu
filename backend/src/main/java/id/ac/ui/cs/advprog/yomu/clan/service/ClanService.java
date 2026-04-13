package id.ac.ui.cs.advprog.yomu.clan.service;

import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.clan.dto.CreateClanRequest;

import java.util.List;

public interface ClanService {
    List<ClanResponse> getAllClans(User currentUser);

    List<ClanResponse> getMyClans(User currentUser);

    ClanResponse createClan(CreateClanRequest request, User currentUser);

    ClanResponse joinClan(Long clanId, User currentUser);
}
