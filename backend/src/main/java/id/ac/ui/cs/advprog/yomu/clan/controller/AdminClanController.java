package id.ac.ui.cs.advprog.yomu.clan.controller;

import id.ac.ui.cs.advprog.yomu.clan.dto.SeasonEndResponse;
import id.ac.ui.cs.advprog.yomu.clan.service.ClanSeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/clans")
@RequiredArgsConstructor
public class AdminClanController {

    private final ClanSeasonService clanSeasonService;

    @PostMapping("/end-season")
    public SeasonEndResponse endSeason() {
        return clanSeasonService.endSeason();
    }
}
