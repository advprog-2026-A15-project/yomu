package id.ac.ui.cs.advprog.yomu.forum.controller;

import id.ac.ui.cs.advprog.yomu.forum.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.forum.dto.CreateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.dto.UpdateCommentRequest;
import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
// @CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<CommentResponse> getAll() {
        return commentService.findAll();
    }

    @PostMapping
    public Comment create(@Valid @RequestBody CreateCommentRequest request) {
        return commentService.create(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        commentService.delete(id);
    }

    @GetMapping("/{id}")
    public Comment getById(@PathVariable UUID id) {
        return commentService.findById(id);
    }

    @PutMapping("/{id}")
    public Comment update(@PathVariable UUID id, @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.update(id, request);
    }
}