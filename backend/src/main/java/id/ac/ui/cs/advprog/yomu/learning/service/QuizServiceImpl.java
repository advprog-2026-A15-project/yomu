package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;
import id.ac.ui.cs.advprog.yomu.learning.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public String cekJawabanKuis(UUID bacaanId, String jawabanUser) {
        // 1. Ambil data kuis dari database
        List<Quiz> kuisList = quizRepository.findByBacaanId(bacaanId);

        if (kuisList.isEmpty()) {
            return "Kuis tidak ditemukan untuk bacaan ini.";
        }

        Quiz kuis = kuisList.get(0);

        // 2. Logika Utama: Mengecek kecocokan jawaban
        if (jawabanUser.toLowerCase().contains(kuis.getJawabanBenar().toLowerCase())) {
            // TODO: Nanti panggil fungsi untuk mengirim Event ke Modul Achievement di sini
            return "Benar! Kuis selesai.";
        } else {
            return "Salah! Silakan coba lagi.";
        }
    }
}