package id.ac.ui.cs.advprog.yomu.forum.service;

import id.ac.ui.cs.advprog.yomu.forum.models.Comment;
import id.ac.ui.cs.advprog.yomu.forum.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomu.forum.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository repository;

    @Override
    public List<Comment> findAll() {
        return repository.findAll();
    }

    @Override
    public Comment create(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Comment findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Komentar tidak ditemukan"));
    }

    @Override
    public Comment update(UUID id, Comment dataBaru) {
        return repository.findById(id).map(comment -> {
            comment.setIsiKomentar(dataBaru.getIsiKomentar());
            return repository.save(comment);
        }).orElseThrow(() -> new RuntimeException("Gagal update: ID tidak ditemukan"));
    }
}
