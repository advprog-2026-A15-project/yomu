package id.ac.ui.cs.advprog.yomu.forum.controller;

import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<Comment> getAll() {
        return commentService.findAll();
    }

    @PostMapping
    public Comment create(@RequestBody Comment comment) {
        return commentService.create(comment);
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
    public Comment update(@PathVariable UUID id, @RequestBody Comment comment) {
        return commentService.update(id, comment);
    }
}