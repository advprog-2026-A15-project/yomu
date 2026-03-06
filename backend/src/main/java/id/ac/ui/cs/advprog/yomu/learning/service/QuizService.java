package id.ac.ui.cs.advprog.yomu.learning.service;

import java.util.UUID;

public interface QuizService {
    String cekJawabanKuis(UUID bacaanId, String jawabanUser);
}