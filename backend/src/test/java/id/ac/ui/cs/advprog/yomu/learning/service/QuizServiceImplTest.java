package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;
import id.ac.ui.cs.advprog.yomu.learning.repository.QuizRepository;
import id.ac.ui.cs.advprog.yomu.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private AchievementService achievementService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    private UUID bacaanId;
    private Quiz dummyQuiz;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        bacaanId = UUID.randomUUID();

        Bacaan dummyBacaan = new Bacaan();
        dummyBacaan.setId(bacaanId);

        dummyQuiz = new Quiz();
        dummyQuiz.setId(UUID.randomUUID());
        dummyQuiz.setBacaan(dummyBacaan);
        dummyQuiz.setPertanyaan("Apa ini?");
        dummyQuiz.setJawabanBenar("hoax");
    }

    @Test
    void testCekJawabanKuis_KuisTidakDitemukan() {
        when(quizRepository.findByBacaanId(bacaanId)).thenReturn(new ArrayList<>());

        String result = quizService.cekJawabanKuis(bacaanId, "jawaban");
        assertEquals("Kuis tidak ditemukan untuk bacaan ini.", result);
    }

    @Test
    void testCekJawabanKuis_JawabanBenar() {
        when(quizRepository.findByBacaanId(bacaanId)).thenReturn(List.of(dummyQuiz));

        String result = quizService.cekJawabanKuis(bacaanId, "Ini berita hoax");
        assertEquals("Benar! Kuis selesai.", result);
    }

    @Test
    void testCekJawabanKuis_JawabanSalah() {
        when(quizRepository.findByBacaanId(bacaanId)).thenReturn(List.of(dummyQuiz));

        String result = quizService.cekJawabanKuis(bacaanId, "jawaban yang salah");
        assertEquals("Salah! Silakan coba lagi.", result);
    }
}
