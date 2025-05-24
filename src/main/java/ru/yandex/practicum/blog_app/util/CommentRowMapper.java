package ru.yandex.practicum.blog_app.util;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.blog_app.model.Comment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentRowMapper implements RowMapper<Comment>  {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setContent(rs.getString("content"));
        comment.setPostId(rs.getLong("post_id"));
        return comment;
    }
}
