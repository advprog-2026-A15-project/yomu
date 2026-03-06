package id.ac.ui.cs.advprog.yomu.clan.controller;

import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.clan.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomu.clan.service.ClanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clans")
@RequiredArgsConstructor
public class ClanController {

    private final ClanService clanService;
    private final UserRepository userRepository;

    @GetMapping
    public List<ClanResponse> getAllClans() {
        return clanService.getAllClans(getCurrentUser());
    }

    @GetMapping("/me")
    public List<ClanResponse> getMyClans() {
        return clanService.getMyClans(getCurrentUser());
    }

    @PostMapping
    public ClanResponse createClan(@Valid @RequestBody CreateClanRequest request) {
        return clanService.createClan(request, getCurrentUser());
    }

    @PostMapping("/{clanId}/join")
    public ClanResponse joinClan(@PathVariable Long clanId) {
        return clanService.joinClan(clanId, getCurrentUser());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }
}
