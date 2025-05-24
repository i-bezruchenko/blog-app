package ru.yandex.practicum.blog_app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.blog_app.consts.SQL;
import ru.yandex.practicum.blog_app.model.Comment;
import ru.yandex.practicum.blog_app.util.CommentRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Comment> findAllCommentsByPostId(Long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("postId", postId);
        return jdbcTemplate.query(SQL.GET_COMMENTS_BY_POST_ID, params, new CommentRowMapper());
    }

    public void deleteCommentsByPostId(Long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("postId", postId);
        jdbcTemplate.update(SQL.DELETE_COMMENTS_BY_POST_ID, params);
    }

    public void addComment(Long postId, String content) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("contentValue", content)
            .addValue("postId", postId);
        jdbcTemplate.update(SQL.ADD_COMMENT, params);
    }

    public void deleteCommentById(Long commentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("commentId", commentId);
        jdbcTemplate.update(SQL.DELETE_COMMENT_BY_ID, params);
    }

    public void updateComment(Long commentId, String content) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("commentId", commentId)
            .addValue("contentValue", content);
        jdbcTemplate.update(SQL.UPDATE_COMMENT, params);
    }
}
