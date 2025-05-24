package ru.yandex.practicum.blog_app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
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
        return jdbcTemplate.query("select id, post_id, content from t_comment where post_id = :postId",
                params, new CommentRowMapper());
    }

    public void deleteCommentsByPostId(Long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("postId", postId);
        jdbcTemplate.update("delete from t_comment where post_id = :postId", params);
    }

    public void addComment(Long postId, String content) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("contentValue", content)
            .addValue("postId", postId);
        jdbcTemplate.update("insert into t_comment (post_id, content) values (:postId, :contentValue)", params);
    }

    public void deleteCommentById(Long commentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("commentId", commentId);
        jdbcTemplate.update("delete from t_comment where id = :commentId", params);
    }

    public void updateComment(Long commentId, String content) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("commentId", commentId)
            .addValue("contentValue", content);
        jdbcTemplate.update("update t_comment set content = :contentValue where id = :commentId", params);
    }
}
