package id.ac.ui.cs.advprog.yomu.learning.service;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.repository.BacaanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class BacaanServiceImpl implements BacaanService {

    @Autowired
    private BacaanRepository repository;

    @Override
    public List<Bacaan> findAll() {
        return repository.findAll();
    }

    @Override
    public Bacaan create(Bacaan bacaan) {
        return repository.save(bacaan);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Bacaan findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Bacaan tidak ditemukan"));
    }

    @Override
    public Bacaan update(UUID id, Bacaan dataBaru) {
        return repository.findById(id).map(bacaan -> {
            bacaan.setJudul(dataBaru.getJudul());
            bacaan.setIsiTeks(dataBaru.getIsiTeks());
            return repository.save(bacaan);
        }).orElseThrow(() -> new RuntimeException("Gagal update: ID tidak ditemukan"));
    }
}