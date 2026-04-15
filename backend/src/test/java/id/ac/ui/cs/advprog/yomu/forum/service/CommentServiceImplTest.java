package id.ac.ui.cs.advprog.yomu.forum.service;

import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.forum.dto.UpdateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository repository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UUID commentId;
    private User owner;
    private User admin;
    private User otherUser;
    private Comment comment;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        commentId = UUID.randomUUID();

        owner = createUser(1L, "owner", Role.PELAJAR);
        admin = createUser(2L, "admin", Role.ADMIN);
        otherUser = createUser(3L, "other", Role.PELAJAR);

        comment = new Comment();
        comment.setId(commentId);
        comment.setIsiKomentar("Komentar awal");
        comment.setUser(owner);
        comment.setUserId(owner.getId());
    }

    @Test
    void update_allowsOwner() {
        setAuthenticatedUser(owner);
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));
        when(repository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("Komentar yang diperbarui");

        Comment result = commentService.update(commentId, request);

        assertEquals("Komentar yang diperbarui", result.getIsiKomentar());
        verify(repository).save(comment);
    }

    @Test
    void update_allowsAdmin() {
        setAuthenticatedUser(admin);
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));
        when(repository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("Komentar dari admin");

        Comment result = commentService.update(commentId, request);

        assertEquals("Komentar dari admin", result.getIsiKomentar());
        verify(repository).save(comment);
    }

    @Test
    void update_deniesOtherUser() {
        setAuthenticatedUser(otherUser);
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("Percobaan update");

        assertThrows(AccessDeniedException.class, () -> commentService.update(commentId, request));
        verify(repository, never()).save(any(Comment.class));
    }

    @Test
    void update_rejectsBlankText() {
        setAuthenticatedUser(owner);
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("   ");

        assertThrows(RuntimeException.class, () -> commentService.update(commentId, request));
        verify(repository, never()).save(any(Comment.class));
    }

    @Test
    void delete_allowsOwner() {
        setAuthenticatedUser(owner);
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.delete(commentId);

        verify(repository).delete(comment);
    }

    @Test
    void delete_allowsAdmin() {
        setAuthenticatedUser(admin);
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.delete(commentId);

        verify(repository).delete(comment);
    }

    @Test
    void delete_deniesOtherUser() {
        setAuthenticatedUser(otherUser);
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(AccessDeniedException.class, () -> commentService.delete(commentId));
        verify(repository, never()).delete(any(Comment.class));
    }

    private void setAuthenticatedUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities())
        );
    }

    private User createUser(Long id, String username, Role role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        return user;
    }
}



