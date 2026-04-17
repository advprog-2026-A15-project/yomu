package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;

import java.util.List;
import java.util.UUID;

public interface QuizService {
    String cekJawabanKuis(UUID quizId, String jawabanUser);
    void selesaikanKuis(UUID bacaanId);

    // Tambahan fungsi untuk fitur Admin (CRUD Kuis)
    Quiz createQuiz(UUID bacaanId, Quiz quiz);
    List<Quiz> getQuizzesByBacaanId(UUID bacaanId);
    Quiz updateQuiz(UUID quizId, Quiz updatedQuiz);
    void deleteQuiz(UUID quizId);
}