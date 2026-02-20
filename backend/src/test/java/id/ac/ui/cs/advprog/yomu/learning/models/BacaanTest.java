package id.ac.ui.cs.advprog.yomu.learning.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class BacaanTest {
    private Bacaan bacaan;

    @BeforeEach
    void setUp() {
        this.bacaan = new Bacaan();
    }

    @Test
    void testGetAndSetJudul() {
        String judul = "Test Judul Bacaan";
        bacaan.setJudul(judul);
        assertEquals(judul, bacaan.getJudul());
    }

    @Test
    void testGetAndSetIsiTeks() {
        String isi = "Ini adalah isi teks untuk pengujian.";
        bacaan.setIsiTeks(isi);
        assertEquals(isi, bacaan.getIsiTeks());
    }

    @Test
    void testGetAndSetId() {
        UUID id = UUID.randomUUID();
        bacaan.setId(id);
        assertEquals(id, bacaan.getId());
    }
}