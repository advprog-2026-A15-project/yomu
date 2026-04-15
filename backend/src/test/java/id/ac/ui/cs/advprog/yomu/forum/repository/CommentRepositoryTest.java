package id.ac.ui.cs.advprog.yomu.forum.repository;

import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.repository.BacaanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BacaanRepository bacaanRepository;

    @Autowired
    private UserRepository userRepository;

    private Bacaan bacaan;
    private User user;

    @BeforeEach
    void setUp() {
        bacaan = new Bacaan();
        bacaan.setJudul("Bacaan Test");
        bacaan.setIsiTeks("Konten Test");
        bacaan = bacaanRepository.save(bacaan);

        user = new User();
        user.setUsername("forum_user");
        user.setDisplayName("Forum User");
        user.setRole(Role.PELAJAR);
        user = userRepository.save(user);
    }

    @Test
    void findAllWithUser_returnsSavedCommentsWithUser() {
        Comment comment = new Comment();
        comment.setIsiKomentar("Komentar utama");
        comment.setBacaan(bacaan);
        comment.setUser(user);
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findAllWithUser();

        assertThat(comments).hasSize(1);
        assertThat(comments.getFirst().getUser().getUsername()).isEqualTo("forum_user");
    }

    @Test
    void findRootCommentsByBacaanId_returnsOnlyRootComments() {
        Comment root = new Comment();
        root.setIsiKomentar("Komentar root");
        root.setBacaan(bacaan);
        root.setUser(user);
        root = commentRepository.save(root);

        Comment reply = new Comment();
        reply.setIsiKomentar("Komentar balasan");
        reply.setBacaan(bacaan);
        reply.setUser(user);
        reply.setParentComment(root);
        commentRepository.save(reply);

        Bacaan otherBacaan = new Bacaan();
        otherBacaan.setJudul("Bacaan lain");
        otherBacaan.setIsiTeks("Konten lain");
        otherBacaan = bacaanRepository.save(otherBacaan);

        Comment otherRoot = new Comment();
        otherRoot.setIsiKomentar("Komentar beda bacaan");
        otherRoot.setBacaan(otherBacaan);
        otherRoot.setUser(user);
        commentRepository.save(otherRoot);

        List<Comment> roots = commentRepository.findRootCommentsByBacaanId(bacaan.getId());

        assertThat(roots).hasSize(1);
        assertThat(roots.getFirst().getId()).isEqualTo(root.getId());
        assertThat(roots.getFirst().getParentId()).isNull();
    }

    @Test
    void findRootCommentsByBacaanId_returnsEmptyWhenNoData() {
        List<Comment> roots = commentRepository.findRootCommentsByBacaanId(UUID.randomUUID());

        assertThat(roots).isEmpty();
    }
}

