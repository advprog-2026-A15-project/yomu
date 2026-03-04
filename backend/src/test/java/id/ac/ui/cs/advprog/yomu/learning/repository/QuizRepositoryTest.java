package id.ac.ui.cs.advprog.yomu.learning.repository;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuizRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private BacaanRepository bacaanRepository;

    @Test
    void testFindByBacaanId() {
        Bacaan bacaan = new Bacaan();
        bacaan.setJudul("Test Bacaan Kuis");
        bacaan.setIsiTeks("Isi Teks");
        Bacaan savedBacaan = bacaanRepository.save(bacaan);

        Quiz quiz = new Quiz();
        quiz.setBacaan(savedBacaan);
        quiz.setPertanyaan("Pertanyaan 1");
        quiz.setJawabanBenar("Jawaban 1");
        quizRepository.save(quiz);

        List<Quiz> foundQuizzes = quizRepository.findByBacaanId(savedBacaan.getId());

        assertThat(foundQuizzes).hasSize(1);
        assertThat(foundQuizzes.get(0).getPertanyaan()).isEqualTo("Pertanyaan 1");
    }
}