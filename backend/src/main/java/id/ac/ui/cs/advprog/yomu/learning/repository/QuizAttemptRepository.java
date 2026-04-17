package id.ac.ui.cs.advprog.yomu.learning.repository;

import org.springframework.data.jpa.repository.Query;
import id.ac.ui.cs.advprog.yomu.learning.models.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;


@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

    boolean existsByUserIdAndQuizId(Long userId, UUID quizId);

    @Modifying // 👇 Tambahkan ini
    @Transactional
    @Query("DELETE FROM QuizAttempt q WHERE q.quiz.id = :quizId") // 👇 Pakai query manual supaya lebih galak
    void deleteByQuizId(UUID quizId);
}