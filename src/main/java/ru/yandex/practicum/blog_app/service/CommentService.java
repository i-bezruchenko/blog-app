package ru.yandex.practicum.blog_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.blog_app.model.Comment;
import ru.yandex.practicum.blog_app.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void addCommentToPost(Long postId, String content) {
        commentRepository.addComment(postId, content);
    }

    public void deleteCommentFromPost(Long commentId) {
        commentRepository.deleteCommentById(commentId);
    }

    public void updateComment(Long commentId, String content) {
        commentRepository.updateComment(commentId, content);
    }

    public List<Comment> findAllCommentsByPostId(Long postId) {
        return commentRepository.findAllCommentsByPostId(postId);
    }

    public void deleteCommentsByPostId(Long postId) {
        commentRepository.deleteCommentsByPostId(postId);
    }
}
