package id.ac.ui.cs.advprog.yomu.learning.controller;

import id.ac.ui.cs.advprog.yomu.learning.models.Quiz;
import id.ac.ui.cs.advprog.yomu.learning.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kuis")
@CrossOrigin(origins = "http://localhost:5173")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/{bacaanId}")
    public Quiz createQuiz(@PathVariable UUID bacaanId, @RequestBody Quiz quiz) {
        return quizService.createQuiz(bacaanId, quiz);
    }

    @GetMapping("/bacaan/{bacaanId}")
    public List<Quiz> getQuizzesByBacaanId(@PathVariable UUID bacaanId) {
        return quizService.getQuizzesByBacaanId(bacaanId);
    }

    @PutMapping("/{quizId}")
    public Quiz updateQuiz(@PathVariable UUID quizId, @RequestBody Quiz quiz) {
        return quizService.updateQuiz(quizId, quiz);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable UUID quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<String> submitJawabanKuis(@PathVariable UUID quizId, @RequestBody String jawabanUser) {
        try {
            // Ini untuk ngecek soal per soal (tanpa ngunci)
            String hasil = quizService.cekJawabanKuis(quizId, jawabanUser);
            return ResponseEntity.ok(hasil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 👇 ENDPOINT BARU UNTUK MENGUNCI KUIS DI AKHIR
    @PostMapping("/bacaan/{bacaanId}/finish")
    public ResponseEntity<String> finishKuisAkhir(@PathVariable UUID bacaanId) {
        try {
            quizService.selesaikanKuis(bacaanId);
            return ResponseEntity.ok("Kuis berhasil diselesaikan dan dikunci.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}