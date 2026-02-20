package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.repository.BacaanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mengaktifkan Mockito
class BacaanServiceImplTest {

    @Mock
    private BacaanRepository repository; // Memalsukan repository

    @InjectMocks
    private BacaanServiceImpl service; // Menyuntikkan mock ke dalam service

    private Bacaan bacaan;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        bacaan = new Bacaan();
        bacaan.setId(id);
        bacaan.setJudul("Judul Test");
        bacaan.setIsiTeks("Konten Test");
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(bacaan));

        List<Bacaan> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getJudul()).isEqualTo("Judul Test");
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        when(repository.findById(id)).thenReturn(Optional.of(bacaan));

        Bacaan result = service.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void testFindById_NotFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(id));
    }

    @Test
    void testCreate() {
        when(repository.save(any(Bacaan.class))).thenReturn(bacaan);

        Bacaan created = service.create(new Bacaan());

        assertThat(created.getJudul()).isEqualTo("Judul Test");
        verify(repository, times(1)).save(any(Bacaan.class));
    }

    @Test
    void testUpdate_Success() {
        Bacaan dataBaru = new Bacaan();
        dataBaru.setJudul("Judul Baru");
        dataBaru.setIsiTeks("Konten Baru");

        when(repository.findById(id)).thenReturn(Optional.of(bacaan));
        when(repository.save(any(Bacaan.class))).thenReturn(bacaan);

        Bacaan updated = service.update(id, dataBaru);

        assertThat(updated.getJudul()).isEqualTo("Judul Baru");
        verify(repository, times(1)).save(bacaan);
    }

    @Test
    void testDelete() {
        service.delete(id);
        verify(repository, times(1)).deleteById(id);
    }
}