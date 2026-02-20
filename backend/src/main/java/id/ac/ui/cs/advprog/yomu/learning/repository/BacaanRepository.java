package id.ac.ui.cs.advprog.yomu.learning.repository;

import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BacaanRepository extends JpaRepository<Bacaan, UUID> {
}