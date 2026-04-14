package id.ac.ui.cs.advprog.yomu.forum.repository;

import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private User savedUser;
    private Bacaan savedBacaan;

    @BeforeEach
    void setUp() {
        // Setup User valid untuk direlasikan
        User user = new User();
        user.setUsername("Nathanael");
        user.setDisplayName("Nathanael Leander");
        user.setPassword("password123");
        user.setRole(Role.PELAJAR);
        savedUser = entityManager.persistAndFlush(user);

        // Setup Bacaan valid untuk direlasikan
        Bacaan bacaan = new Bacaan();
        bacaan.setJudul("Example Judul");
        bacaan.setIsiTeks("Example Teks");
        savedBacaan = entityManager.persistAndFlush(bacaan);
    }

    @Test
    void testFindAllWithUser_ReturnsCommentsAndFetchedUser() {
        // Arrange
        Comment comment = new Comment();
        comment.setIsiKomentar("Komentar dengan user");
        comment.setUser(savedUser);
        comment.setBacaan(savedBacaan);
        entityManager.persistAndFlush(comment);
        entityManager.clear(); // Bersihkan cache hibernate agar memaksa query select dari DB

        // Act
        List<Comment> results = commentRepository.findAllWithUser();

        // Assert
        assertFalse(results.isEmpty());
        assertEquals("Nathanael", results.get(0).getUser().getUsername());
    }

    @Test
    void testFindRootCommentsByBacaanId_ReturnsOnlyRootComments() {
        // Arrange: Buat Root Comment
        Comment rootComment = new Comment();
        rootComment.setIsiKomentar("Ini Root Comment");
        rootComment.setUser(savedUser);
        rootComment.setBacaan(savedBacaan);
        Comment savedRoot = entityManager.persistAndFlush(rootComment);

        // Arrange: Buat Child Comment (Reply)
        Comment childComment = new Comment();
        childComment.setIsiKomentar("Ini Balasan (Child)");
        childComment.setUser(savedUser);
        childComment.setBacaan(savedBacaan);
        childComment.setParentComment(savedRoot); // Set referensi ke root comment
        entityManager.persistAndFlush(childComment);

        entityManager.clear(); // Bersihkan cache memori sementara

        // Act: Panggil metode repositori spesifik
        List<Comment> rootComments = commentRepository.findRootCommentsByBacaanId(savedBacaan.getId());

        // Assert
        assertNotNull(rootComments);
        assertEquals(1, rootComments.size(), "Hanya boleh mengembalikan 1 komentar (karena yang satu lagi adalah balasan)");

        Comment fetchedRoot = rootComments.get(0);
        assertEquals("Ini Root Comment", fetchedRoot.getIsiKomentar());
        assertNull(fetchedRoot.getParentComment(), "Root comment seharusnya tidak memiliki parent");
    }

    @Test
    void testFindRootCommentsByBacaanId_DifferentBacaan_ReturnsEmpty() {
        // Arrange
        Comment rootComment = new Comment();
        rootComment.setIsiKomentar("Komentar di bacaan X");
        rootComment.setUser(savedUser);
        rootComment.setBacaan(savedBacaan);
        entityManager.persistAndFlush(rootComment);

        UUID unknownBacaanId = UUID.randomUUID();

        // Act
        List<Comment> rootComments = commentRepository.findRootCommentsByBacaanId(unknownBacaanId);

        // Assert
        assertTrue(rootComments.isEmpty(), "Seharusnya mengembalikan list kosong karena ID bacaan tidak cocok");
    }
}