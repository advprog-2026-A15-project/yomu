package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;
import id.ac.ui.cs.advprog.yomu.learning.models.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.learning.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomu.learning.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.learning.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private BacaanRepository bacaanRepository;
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public String cekJawabanKuis(UUID quizId, String jawabanUser) {
        Quiz kuis = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Soal Kuis tidak ditemukan"));

        // 👇 HANYA MENGECEK BENAR/SALAH (TIDAK MENYIMPAN KE DATABASE)
        // Karena tidak disimpan, aturan larangan tidak akan terpicu di tengah jalan
        String normalizedUserAnswer = jawabanUser == null ? "" : jawabanUser.trim().toLowerCase(java.util.Locale.ROOT);
        String normalizedCorrectAnswer = kuis.getJawabanBenar().toLowerCase(java.util.Locale.ROOT);

        if (normalizedUserAnswer.contains(normalizedCorrectAnswer)) {
            return "Benar!";
        } else {
            return "Salah! Silakan coba lagi.";
        }
    }


    @Override
    @Transactional
    public void selesaikanKuis(UUID bacaanId) {
        User currentUser = getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("Kamu harus login"));

        Bacaan bacaan = bacaanRepository.findById(bacaanId)
                .orElseThrow(() -> new RuntimeException("Bacaan tidak ditemukan"));

        // Simpan riwayat pengerjaan.
        // INILAH YANG AKAN MEMICU ATURAN "TIDAK BISA MEMBACA KUIS LAGI" MILIKMU!
        for (Quiz q : bacaan.getQuizzes()) {
            if (!quizAttemptRepository.existsByUserIdAndQuizId(currentUser.getId(), q.getId())) {
                QuizAttempt attempt = new QuizAttempt();
                attempt.setUserId(currentUser.getId());
                attempt.setQuiz(q);
                quizAttemptRepository.save(attempt);
            }
        }

        // Buka achievement
        achievementService.unlockFirstReadAchievement(currentUser);
    }

    // --- CRUD KUIS ---

    @Override
    public Quiz createQuiz(UUID bacaanId, Quiz quizForm) {
        Bacaan bacaan = bacaanRepository.findById(bacaanId)
                .orElseThrow(() -> new RuntimeException("Bacaan tidak ditemukan"));

        // Buat objek Kuis yang benar-benar baru
        Quiz kuisBaru = new Quiz();
        kuisBaru.setBacaan(bacaan);

        // Pindahkan data dari form Frontend ke objek baru
        kuisBaru.setPertanyaan(quizForm.getPertanyaan());
        kuisBaru.setJawabanBenar(quizForm.getJawabanBenar());

        // Simpan ke database
        return quizRepository.save(kuisBaru);
    }

    @Override
    public List<Quiz> getQuizzesByBacaanId(UUID bacaanId) {
        return quizRepository.findByBacaanId(bacaanId);
    }

    @Override
    public Quiz updateQuiz(UUID quizId, Quiz updatedQuiz) {
        return quizRepository.findById(quizId).map(quiz -> {
            quiz.setPertanyaan(updatedQuiz.getPertanyaan());
            quiz.setJawabanBenar(updatedQuiz.getJawabanBenar());
            return quizRepository.save(quiz);
        }).orElseThrow(() -> new RuntimeException("Gagal update: Kuis tidak ditemukan"));
    }

    @Override
    @Transactional
    public void deleteQuiz(UUID quizId) {
        // 1. Cari kuisnya dulu
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Kuis tidak ditemukan"));

        // 2. Ambil Bacaan (Parent)-nya
        Bacaan bacaan = quiz.getBacaan();

        // 3. PUTUSKAN HUBUNGAN: Hapus kuis dari list milik Bacaan
        // Ini penting supaya Hibernate tidak mencoba menyimpan kuis ini lagi
        if (bacaan != null && bacaan.getQuizzes() != null) {
            bacaan.getQuizzes().remove(quiz);
        }

        // 4. Hapus history kuis (QuizAttempt) secara manual dulu
        quizAttemptRepository.deleteByQuizId(quizId);

        // 5. Baru hapus kuisnya dari repository
        quizRepository.delete(quiz);
    }

    private Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return Optional.of(user);
        }
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername());
        }
        return Optional.empty();
    }
}