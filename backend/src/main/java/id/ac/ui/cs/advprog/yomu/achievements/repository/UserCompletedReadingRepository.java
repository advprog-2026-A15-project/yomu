package id.ac.ui.cs.advprog.yomu.achievements.repository;

import id.ac.ui.cs.advprog.yomu.achievements.model.UserCompletedReading;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserCompletedReadingRepository extends JpaRepository<UserCompletedReading, Long> {

    boolean existsByUserAndBacaanId(User user, UUID bacaanId);
    long countByUser(User user);
    long countByUserAndCompletedAtGreaterThanEqualAndCompletedAtLessThan(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );
}
