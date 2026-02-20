package id.ac.ui.cs.advprog.yomu.learning.repository;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BacaanRepositoryTest {

    @Autowired
    private BacaanRepository bacaanRepository;

    @Test
    void testSaveAndFindById() {
        Bacaan bacaan = new Bacaan();
        bacaan.setJudul("Tutorial Testing");
        bacaan.setIsiTeks("Ini adalah konten uji coba.");

        Bacaan savedBacaan = bacaanRepository.save(bacaan);

        Optional<Bacaan> foundBacaan = bacaanRepository.findById(savedBacaan.getId());

        assertThat(foundBacaan).isPresent();
        assertThat(foundBacaan.get().getJudul()).isEqualTo("Tutorial Testing");
        assertThat(foundBacaan.get().getId()).isInstanceOf(UUID.class);
    }

    @Test
    void testDeleteById() {
        Bacaan bacaan = new Bacaan();
        bacaan.setJudul("Akan Dihapus");
        bacaan.setIsiTeks("Konten...");
        Bacaan saved = bacaanRepository.save(bacaan);
        UUID id = saved.getId();

        bacaanRepository.deleteById(id);
        Optional<Bacaan> deleted = bacaanRepository.findById(id);

        assertThat(deleted).isEmpty();
    }
}