package id.ac.ui.cs.advprog.yomu.learning.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizTest {
    private Quiz quiz;
    private Bacaan bacaan;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        bacaan = new Bacaan();
        bacaan.setId(UUID.randomUUID());
    }

    @Test
    void testGetAndSetPertanyaan() {
        quiz.setPertanyaan("Apa itu hoax?");
        assertEquals("Apa itu hoax?", quiz.getPertanyaan());
    }

    @Test
    void testGetAndSetJawabanBenar() {
        quiz.setJawabanBenar("berita palsu");
        assertEquals("berita palsu", quiz.getJawabanBenar());
    }

    @Test
    void testGetAndSetBacaan() {
        quiz.setBacaan(bacaan);
        assertEquals(bacaan, quiz.getBacaan());
    }

    @Test
    void testGetAndSetId() {
        UUID id = UUID.randomUUID();
        quiz.setId(id);
        assertEquals(id, quiz.getId());
    }
}