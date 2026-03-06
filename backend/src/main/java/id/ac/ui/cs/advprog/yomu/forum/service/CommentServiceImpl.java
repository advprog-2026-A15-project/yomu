package id.ac.ui.cs.advprog.yomu.forum.service;

import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.forum.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.forum.dto.CreateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomu.learning.models.Bacaan;
import id.ac.ui.cs.advprog.yomu.learning.repository.BacaanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
        comment.setIsiKomentar(request.getIsiKomentar());
        comment.setBacaan(bacaan);
        comment.setUser(user);
        return repository.save(comment);
    }

    @Override
    public Comment findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Komentar tidak ditemukan"));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Comment update(UUID id, Comment dataBaru) {
        return repository.findById(id).map(comment -> {
            comment.setIsiKomentar(dataBaru.getIsiKomentar());
            return repository.save(comment);
        }).orElseThrow(() -> new RuntimeException("Gagal update: ID tidak ditemukan"));
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
        return response;
    }
}
