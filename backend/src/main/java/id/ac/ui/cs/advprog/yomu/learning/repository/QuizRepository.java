package id.ac.ui.cs.advprog.yomu.learning.repository;

import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    // Fungsi khusus untuk mencari kuis berdasarkan ID Bacaan
    List<Quiz> findByBacaanId(UUID bacaanId);
}