package id.ac.ui.cs.advprog.yomu.learning.controller;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.service.BacaanService;
import id.ac.ui.cs.advprog.yomu.learning.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bacaan")
// @CrossOrigin(origins = "http://localhost:5173")
public class BacaanController {

    @Autowired
    private BacaanService bacaanService;

    @Autowired
    private QuizService quizService;

    // Menampilkan daftar bacaan. Kalau ada param ?kategori=Fiksi, maka akan difilter.
    @GetMapping
    public List<Bacaan> getAll(@RequestParam(required = false) String kategori) {
        List<Bacaan> semuaBacaan = bacaanService.findAll();
        if (kategori != null && !kategori.trim().isEmpty()) {
            return semuaBacaan.stream()
                    .filter(b -> kategori.equalsIgnoreCase(b.getKategori()))
                    .collect(Collectors.toList());
        }
        return semuaBacaan;
    }

    @GetMapping("/{id}")
    public Bacaan getById(@PathVariable UUID id) {
        return bacaanService.findById(id);
    }

    @PostMapping
    public Bacaan create(@RequestBody Bacaan bacaan) {
        return bacaanService.create(bacaan);
    }

    @PutMapping("/{id}")
    public Bacaan update(@PathVariable UUID id, @RequestBody Bacaan bacaan) {
        return bacaanService.update(id, bacaan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bacaanService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{bacaanId}/kuis/submit")
    public ResponseEntity<String> submitKuis(@PathVariable UUID bacaanId, @RequestBody String jawaban) {
        String hasil = quizService.cekJawabanKuis(bacaanId, jawaban);
        return ResponseEntity.ok(hasil);
    }
}