package id.ac.ui.cs.advprog.yomu.forum.service;

import id.ac.ui.cs.advprog.yomu.forum.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.forum.dto.CreateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    List<CommentResponse> findAll();
    Comment findById(UUID id); // Untuk mengambil data lama
    Comment create(CreateCommentRequest request);
    Comment update(UUID id, Comment comment); // Untuk menyimpan perubahan
    void delete(UUID id);
}
