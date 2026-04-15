package id.ac.ui.cs.advprog.yomu.forum.service;

import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.forum.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.forum.dto.CreateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.dto.UpdateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.repository.BacaanRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository repository;

    @Mock
    private BacaanRepository bacaanRepository;

    @Mock
    private UserRepository userRepository;

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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAll_mapsCommentAndReplies() {
        Comment reply = new Comment();
        reply.setId(UUID.randomUUID());
        reply.setIsiKomentar("balasan");
        reply.setBacaanId(UUID.randomUUID());
        reply.setUser(owner);

        comment.setBacaanId(UUID.randomUUID());
        comment.setReplies(List.of(reply));

        when(repository.findAllWithUser()).thenReturn(List.of(comment));

        List<CommentResponse> responses = commentService.findAll();

        assertEquals(1, responses.size());
        assertEquals("owner", responses.getFirst().getUsername());
        assertEquals(1, responses.getFirst().getReplies().size());
        assertEquals("balasan", responses.getFirst().getReplies().getFirst().getIsiKomentar());
    }

    @Test
    void findAll_usesUnknownWhenUserIsMissing() {
        comment.setUser(null);
        when(repository.findAllWithUser()).thenReturn(List.of(comment));

        List<CommentResponse> responses = commentService.findAll();

        assertEquals("Unknown", responses.getFirst().getUsername());
    }

    @Test
    void findById_returnsCommentWhenFound() {
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.findById(commentId);

        assertEquals(commentId, result.getId());
    }

    @Test
    void findById_throwsWhenMissing() {
        when(repository.findById(commentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.findById(commentId));

        assertEquals("Komentar tidak ditemukan", exception.getMessage());
    }

    @Test
    void create_savesCommentWithoutParent() {
        UUID bacaanId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);
        setAuthenticatedUser(owner);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setIsiKomentar("  komentar baru  ");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));
        when(repository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.create(request);

        assertEquals("komentar baru", result.getIsiKomentar());
        assertEquals(bacaan, result.getBacaan());
        assertEquals(owner, result.getUser());
        assertEquals(null, result.getParentComment());
    }

    @Test
    void create_savesCommentWithParent() {
        UUID bacaanId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);

        Comment parent = new Comment();
        parent.setId(parentId);

        setAuthenticatedUser(owner);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setParentCommentId(parentId);
        request.setIsiKomentar("isi");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));
        when(repository.findById(parentId)).thenReturn(Optional.of(parent));
        when(repository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.create(request);

        assertEquals(parent, result.getParentComment());
    }

    @Test
    void create_throwsWhenBacaanMissing() {
        setAuthenticatedUser(owner);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(UUID.randomUUID());
        request.setIsiKomentar("isi");

        when(bacaanRepository.findById(request.getBacaanId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.create(request));

        assertEquals("Bacaan tidak ditemukan", exception.getMessage());
    }

    @Test
    void create_throwsWhenParentMissing() {
        UUID bacaanId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);

        setAuthenticatedUser(owner);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setParentCommentId(parentId);
        request.setIsiKomentar("isi");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));
        when(repository.findById(parentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.create(request));

        assertEquals("Komentar induk tidak ditemukan", exception.getMessage());
    }

    @Test
    void create_throwsWhenTextBlankAfterTrim() {
        UUID bacaanId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);

        setAuthenticatedUser(owner);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setIsiKomentar("   ");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.create(request));

        assertEquals("Isi komentar tidak boleh kosong", exception.getMessage());
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
    void update_deniesWhenAuthorComesFromUserIdAndNotOwner() {
        setAuthenticatedUser(otherUser);
        comment.setUser(null);
        comment.setUserId(owner.getId());
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("Percobaan update");

        assertThrows(AccessDeniedException.class, () -> commentService.update(commentId, request));
    }

    @Test
    void update_throwsWhenIdMissing() {
        setAuthenticatedUser(owner);
        when(repository.findById(commentId)).thenReturn(Optional.empty());

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("isi");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.update(commentId, request));

        assertEquals("Gagal update: ID tidak ditemukan", exception.getMessage());
    }

    @Test
    void update_rejectsBlankText() {
        setAuthenticatedUser(owner);
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setIsiKomentar("   ");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.update(commentId, request));

        assertEquals("Isi komentar tidak boleh kosong", exception.getMessage());
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

    @Test
    void delete_throwsWhenCommentMissing() {
        setAuthenticatedUser(owner);
        when(repository.findById(commentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.delete(commentId));

        assertEquals("Komentar tidak ditemukan", exception.getMessage());
    }

    @Test
    void create_throwsWhenAuthenticationMissing() {
        UUID bacaanId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setIsiKomentar("isi");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.create(request));

        assertEquals("User belum login", exception.getMessage());
    }

    @Test
    void create_throwsWhenAnonymousAuthentication() {
        UUID bacaanId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);

        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymousUser",
                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                )
        );

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setIsiKomentar("isi");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.create(request));

        assertEquals("User belum login", exception.getMessage());
    }

    @Test
    void create_usesUserDetailsPrincipal() {
        UUID bacaanId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("owner")
                .password("secret")
                .authorities("ROLE_PELAJAR")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "secret", principal.getAuthorities())
        );

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setIsiKomentar("isi");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
        when(repository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.create(request);

        assertEquals(owner, result.getUser());
    }

    @Test
    void create_throwsWhenUserDetailsNotFoundInRepository() {
        UUID bacaanId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("missing")
                .password("secret")
                .authorities("ROLE_PELAJAR")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "secret", principal.getAuthorities())
        );

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setIsiKomentar("isi");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.create(request));

        assertEquals("User tidak ditemukan", exception.getMessage());
    }

    @Test
    void create_throwsWhenPrincipalTypeInvalid() {
        UUID bacaanId = UUID.randomUUID();
        Bacaan bacaan = new Bacaan();
        bacaan.setId(bacaanId);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "bad-principal",
                        "secret",
                        List.of(new SimpleGrantedAuthority("ROLE_PELAJAR"))
                )
        );

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBacaanId(bacaanId);
        request.setIsiKomentar("isi");

        when(bacaanRepository.findById(bacaanId)).thenReturn(Optional.of(bacaan));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> commentService.create(request));

        assertEquals("Data user tidak valid", exception.getMessage());
    }

    @Test
    void findAll_returnsEmptyListWhenRepositoryEmpty() {
        when(repository.findAllWithUser()).thenReturn(List.of());

        List<CommentResponse> result = commentService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
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


