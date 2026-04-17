package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;
import id.ac.ui.cs.advprog.yomu.learning.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String cekJawabanKuis(UUID bacaanId, String jawabanUser) {
        // 1. Ambil data kuis dari database
        List<Quiz> kuisList = quizRepository.findByBacaanId(bacaanId);

        if (kuisList.isEmpty()) {
            return "Kuis tidak ditemukan untuk bacaan ini.";
        }

        Quiz kuis = kuisList.get(0);

        // 2. Logika Utama: Mengecek kecocokan jawaban
        String normalizedUserAnswer = jawabanUser == null ? "" : jawabanUser.trim().toLowerCase(Locale.ROOT);
        String normalizedCorrectAnswer = kuis.getJawabanBenar().toLowerCase(Locale.ROOT);

        if (normalizedUserAnswer.contains(normalizedCorrectAnswer)) {
            getAuthenticatedUser().ifPresent(user -> achievementService.recordCompletedReading(user, bacaanId));
            return "Benar! Kuis selesai.";
        } else {
            return "Salah! Silakan coba lagi.";
        }
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
