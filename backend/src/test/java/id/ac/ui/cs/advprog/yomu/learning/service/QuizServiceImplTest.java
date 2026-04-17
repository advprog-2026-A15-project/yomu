package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;
import id.ac.ui.cs.advprog.yomu.learning.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private UUID quizId;
    private Quiz dummyQuiz;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        bacaanId = UUID.randomUUID();
        quizId = UUID.randomUUID();

        Bacaan dummyBacaan = new Bacaan();
        dummyBacaan.setId(bacaanId);

        dummyQuiz = new Quiz();
        dummyQuiz.setId(quizId);
        dummyQuiz.setBacaan(dummyBacaan);
        dummyQuiz.setPertanyaan("Apa ini?");
        dummyQuiz.setJawabanBenar("hoax");
    }

    @Test
    void testCekJawabanKuis_KuisTidakDitemukan() {
        UUID missingQuizId = UUID.randomUUID();
        when(quizRepository.findById(missingQuizId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> quizService.cekJawabanKuis(missingQuizId, "jawaban")
        );
        assertEquals("Soal Kuis tidak ditemukan", exception.getMessage());
    }

    @Test
    void testCekJawabanKuis_JawabanBenar() {
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(dummyQuiz));

        String result = quizService.cekJawabanKuis(quizId, "Ini berita hoax");
        assertEquals("Benar!", result);
    }

    @Test
    void testCekJawabanKuis_JawabanSalah() {
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(dummyQuiz));

        String result = quizService.cekJawabanKuis(quizId, "jawaban yang salah");
        assertEquals("Salah! Silakan coba lagi.", result);
    }
}
