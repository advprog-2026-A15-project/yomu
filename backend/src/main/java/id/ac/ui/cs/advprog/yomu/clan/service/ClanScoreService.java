package id.ac.ui.cs.advprog.yomu.clan.service;

import id.ac.ui.cs.advprog.yomu.clan.model.Clan;

public interface ClanScoreService {
    long calculateClanScore(Clan clan);
    void refreshClanScore(Clan clan);
    void refreshAllClanScores();
}
