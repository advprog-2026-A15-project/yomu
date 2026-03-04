package id.ac.ui.cs.advprog.yomu.learning.controller;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.service.BacaanService;
import id.ac.ui.cs.advprog.yomu.learning.service.QuizService; // Import Interface Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bacaan")
@CrossOrigin(origins = "http://localhost:5173")
public class BacaanController {

    @Autowired
    private BacaanService bacaanService;

    @Autowired
    private QuizService quizService;

    @GetMapping
    public List<Bacaan> getAll() {
        return bacaanService.findAll();
    }

    @PostMapping
    public Bacaan create(@RequestBody Bacaan bacaan) {
        return bacaanService.create(bacaan);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        bacaanService.delete(id);
    }

    @GetMapping("/{id}")
    public Bacaan getById(@PathVariable UUID id) {
        return bacaanService.findById(id);
    }

    @PutMapping("/{id}")
    public Bacaan update(@PathVariable UUID id, @RequestBody Bacaan bacaan) {
        return bacaanService.update(id, bacaan);
    }

    @PostMapping("/{bacaanId}/kuis/submit")
    public String submitKuis(@PathVariable UUID bacaanId, @RequestBody String jawabanUser) {
        // Controller HANYA melempar data ke Service, lalu mengembalikan hasilnya
        return quizService.cekJawabanKuis(bacaanId, jawabanUser);
    }
}