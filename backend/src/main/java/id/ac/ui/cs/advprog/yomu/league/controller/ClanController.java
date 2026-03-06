package id.ac.ui.cs.advprog.yomu.league.controller;

import id.ac.ui.cs.advprog.yomu.league.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomu.league.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.league.service.ClanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// Import your auth module's User model to get the ID from the principal
import id.ac.ui.cs.advprog.yomu.authentication.model.User;

@RestController
@RequestMapping("/api/clans")
@RequiredArgsConstructor
public class ClanController {

    private final ClanService clanService;
    @PostMapping
    public ResponseEntity<ClanResponse> createClan(
            @RequestBody CreateClanRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        ClanResponse response = clanService.createClan(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}