package id.ac.ui.cs.advprog.yomu.forum.service;

import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.forum.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.forum.dto.CreateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.repository.BacaanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BacaanRepository bacaanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User mockUser;
    private Bacaan mockBacaan;

    @BeforeEach
    void setUp() {
        // Setup user otentikasi
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Naeru");
        mockUser.setRole(Role.PELAJAR);

        // Mock Security Context (karena CommentServiceImpl mengambil user dari SecurityContextHolder)
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities())
        );

        // Setup Bacaan
        mockBacaan = new Bacaan();
        mockBacaan.setId(UUID.randomUUID());
        mockBacaan.setJudul("Materi Arsitektur RMR");
    }

    @Test
    void testCreateRootComment_Success() {
        // Arrange
        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar("Komentar tingkat atas (root)");
        request.setBacaanId(mockBacaan.getId());
        // Tidak ada parentCommentId

        when(bacaanRepository.findById(request.getBacaanId())).thenReturn(Optional.of(mockBacaan));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Comment result = commentService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("Komentar tingkat atas (root)", result.getIsiKomentar());
        assertNull(result.getParentComment()); // Harus null karena bukan balasan
        assertEquals(mockUser, result.getUser());
        assertEquals(mockBacaan, result.getBacaan());

        verify(commentRepository, never()).findById(any(UUID.class)); // Pastikan tidak mencari parent
    }

    @Test
    void testCreateNestedComment_Success() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        Comment parentComment = new Comment();
        parentComment.setId(parentId);
        parentComment.setIsiKomentar("Komentar Induk");

        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar("Ini adalah sebuah balasan.");
        request.setBacaanId(mockBacaan.getId());
        request.setParentCommentId(parentId); // Set parent ID

        when(bacaanRepository.findById(request.getBacaanId())).thenReturn(Optional.of(mockBacaan));
        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Comment result = commentService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("Ini adalah sebuah balasan.", result.getIsiKomentar());
        assertNotNull(result.getParentComment());
        assertEquals(parentId, result.getParentComment().getId()); // Parent-nya harus sama
        assertEquals(parentComment, result.getParentComment());
    }

    @Test
    void testCreateNestedComment_ParentNotFound_ThrowsException() {
        // Arrange
        UUID invalidParentId = UUID.randomUUID();
        CreateCommentRequest request = new CreateCommentRequest();
        request.setIsiKomentar("Balasan untuk entitas yang tidak ada");
        request.setBacaanId(mockBacaan.getId());
        request.setParentCommentId(invalidParentId);

        when(bacaanRepository.findById(request.getBacaanId())).thenReturn(Optional.of(mockBacaan));
        when(commentRepository.findById(invalidParentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.create(request);
        });

        assertEquals("Komentar induk tidak ditemukan", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testFindAll_RecursiveMapping_Success() {
        // Arrange
        Comment rootComment = new Comment();
        rootComment.setId(UUID.randomUUID());
        rootComment.setIsiKomentar("Komentar Utama");
        rootComment.setUser(mockUser);
        rootComment.setBacaanId(mockBacaan.getId());
        rootComment.setCreatedAt(LocalDateTime.now());

        Comment childComment = new Comment();
        childComment.setId(UUID.randomUUID());
        childComment.setIsiKomentar("Balasan Pertama");
        childComment.setUser(mockUser);
        childComment.setParentId(rootComment.getId());
        childComment.setParentComment(rootComment);
        childComment.setCreatedAt(LocalDateTime.now());

        // Simulasi relasi bersarang yang dikembalikan oleh database
        rootComment.getReplies().add(childComment);

        when(commentRepository.findAllWithUser()).thenReturn(List.of(rootComment));

        // Act
        List<CommentResponse> responses = commentService.findAll();

        // Assert
        assertEquals(1, responses.size(), "Seharusnya mengembalikan 1 komentar tingkat atas");

        CommentResponse rootResponse = responses.get(0);
        assertEquals(rootComment.getId(), rootResponse.getId());
        assertEquals("Naeru", rootResponse.getUsername());

        // Memeriksa pemetaan bersarang (nested)
        assertNotNull(rootResponse.getReplies());
        assertEquals(1, rootResponse.getReplies().size(), "Komentar root harusnya punya 1 balasan");

        CommentResponse childResponse = rootResponse.getReplies().get(0);
        assertEquals("Balasan Pertama", childResponse.getIsiKomentar());
        assertEquals(rootResponse.getId(), childResponse.getParentId());
        assertEquals("Naeru", childResponse.getUsername());
    }
}