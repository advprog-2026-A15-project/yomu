package id.ac.ui.cs.advprog.yomu.forum.repository;

import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user")
    List<Comment> findAllWithUser();

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.user WHERE c.bacaan.id = :bacaanId AND c.parentComment IS NULL")
    List<Comment> findRootCommentsByBacaanId(@Param("bacaanId") UUID bacaanId);
}