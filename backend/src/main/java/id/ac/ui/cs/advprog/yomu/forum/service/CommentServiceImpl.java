package id.ac.ui.cs.advprog.yomu.forum.service;

import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.forum.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.forum.dto.CreateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.dto.UpdateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.repository.BacaanRepository;
import lombok.RequiredArgsConstructor;
import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final BacaanRepository bacaanRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentResponse> findAll() {
        return repository.findAllWithUser().stream()
                .map(this::mapToCommentResponse)
                .toList();
    }

    @Override
    public Comment create(CreateCommentRequest request) {
        Bacaan bacaan = bacaanRepository.findById(request.getBacaanId())
                .orElseThrow(() -> new RuntimeException("Bacaan tidak ditemukan"));

        User user = getAuthenticatedUser();

        Comment comment = new Comment();
        comment.setIsiKomentar(normalizeCommentText(request.getIsiKomentar()));
        comment.setBacaan(bacaan);
        comment.setUser(user);

        // Cek apakah ini merupakan balasan dari komentar lain
        if (request.getParentCommentId() != null) {
            Comment parent = repository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Komentar induk tidak ditemukan"));
            comment.setParentComment(parent);
        }

        return repository.save(comment);
    }

    @Override
    public Comment findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Komentar tidak ditemukan"));
    }

    @Override
    public void delete(UUID id) {
        Comment comment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Komentar tidak ditemukan"));
        assertCanModifyComment(comment);
        repository.delete(comment);
    }

    @Override
    public Comment update(UUID id, UpdateCommentRequest request) {
        return repository.findById(id).map(comment -> {
            assertCanModifyComment(comment);
            comment.setIsiKomentar(normalizeCommentText(request.getIsiKomentar()));
            return repository.save(comment);
        }).orElseThrow(() -> new RuntimeException("Gagal update: ID tidak ditemukan"));
    }

    private String normalizeCommentText(String text) {
        String normalized = Objects.requireNonNullElse(text, "").trim();
        if (normalized.isEmpty()) {
            throw new RuntimeException("Isi komentar tidak boleh kosong");
        }
        return normalized;
    }

    private void assertCanModifyComment(Comment comment) {
        User currentUser = getAuthenticatedUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        Long commentAuthorId = comment.getUser() != null ? comment.getUser().getId() : comment.getUserId();
        if (commentAuthorId == null || !commentAuthorId.equals(currentUser.getId())) {
            throw new AccessDeniedException("Hanya admin atau penulis komentar yang dapat mengubah komentar");
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("User belum login");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        }

        throw new RuntimeException("Data user tidak valid");
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setIsiKomentar(comment.getIsiKomentar());
        response.setBacaanId(comment.getBacaanId());
        response.setUsername(comment.getUser() != null ? comment.getUser().getUsername() : "Unknown");
        response.setCreatedAt(comment.getCreatedAt());
        response.setParentId(comment.getParentId());

        // Mapping balasan (replies) secara rekursif
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<CommentResponse> replyResponses = comment.getReplies().stream()
                    .map(this::mapToCommentResponse) // Pemanggilan rekursif
                    .toList();
            response.setReplies(replyResponses);
        }

        return response;
    }
}
