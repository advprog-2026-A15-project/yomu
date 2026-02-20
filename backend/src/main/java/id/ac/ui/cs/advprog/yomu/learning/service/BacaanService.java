package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import java.util.List;
import java.util.UUID;

public interface BacaanService {
    List<Bacaan> findAll();
    Bacaan findById(UUID id); // Untuk mengambil data lama
    Bacaan create(Bacaan bacaan);
    Bacaan update(UUID id, Bacaan bacaan); // Untuk menyimpan perubahan
    void delete(UUID id);
}