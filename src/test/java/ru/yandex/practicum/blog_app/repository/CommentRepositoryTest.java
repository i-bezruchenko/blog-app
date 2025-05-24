package ru.yandex.practicum.blog_app.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.blog_app.config.DataSourceConfig;
import ru.yandex.practicum.blog_app.model.Comment;
import ru.yandex.practicum.blog_app.util.CommentRowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(DataSourceConfig.class)
class CommentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM t_comment");
        jdbcTemplate.execute("ALTER TABLE t_comment ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("DELETE FROM t_post_tag");
        jdbcTemplate.execute("DELETE FROM t_post");
        jdbcTemplate.execute("DELETE FROM t_tag");
        jdbcTemplate.execute("ALTER TABLE t_post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE t_tag ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("INSERT INTO t_post (title, image, content, likes_count) VALUES (?, ?, ?, ?)",
                "Post 1", "Post Image Content One".getBytes(), "Post Text1", 10);
        jdbcTemplate.update("INSERT INTO t_post (title, image, content, likes_count) VALUES (?, ?, ?, ?)",
                "Post 2", "Post Image Content Two".getBytes(), "Post Text2", 5);
        jdbcTemplate.update("INSERT INTO t_tag (name) VALUES (?)", "Tag1");
        jdbcTemplate.update("INSERT INTO t_tag (name) VALUES (?)", "Tag2");
        jdbcTemplate.update("INSERT INTO t_tag (name) VALUES (?)", "Tag3");
        jdbcTemplate.update("INSERT INTO t_post_tag (post_id, tag_id) VALUES (?, ?)",
                1L, 1L);
        jdbcTemplate.update("INSERT INTO t_post_tag (post_id, tag_id) VALUES (?, ?)",
                1L, 2L);
        jdbcTemplate.update("INSERT INTO t_post_tag (post_id, tag_id) VALUES (?, ?)",
                2L, 2L);
        jdbcTemplate.update("INSERT INTO t_post_tag (post_id, tag_id) VALUES (?, ?)",
                2L, 3L);
        jdbcTemplate.execute("INSERT INTO t_comment (post_id, content) VALUES (1, 'Comment 1')");
        jdbcTemplate.execute("INSERT INTO t_comment (post_id, content) VALUES (1, 'Comment 2')");
        jdbcTemplate.execute("INSERT INTO t_comment (post_id, content) VALUES (2, 'Comment 3')");
    }

    @Test
    void findAllCommentsByPostId_shouldReturnAllCommentsByPostId() {
        var expected = List.of(
                new Comment(1L, 1L, "Comment 1"),
                new Comment(2L, 1L, "Comment 2")
        );
        var postId = 1L;

        List<Comment> founded = commentRepository.findAllCommentsByPostId(postId);

        assertNotNull(founded);
        assertEquals(expected.size(), founded.size());
        assertArrayEquals(expected.toArray(), founded.toArray());
    }

    @Test
    void deleteCommentsByPostId_shouldRemoveAllCommentsByPostId() {
        var postId = 2L;

        commentRepository.deleteCommentsByPostId(postId);

        var founded = commentRepository.findAllCommentsByPostId(postId);

        assertNotNull(founded);
        assertEquals(0, founded.size());
    }

    @Test
    void addComment_shouldAddCommentToDb() {
        var postId = 1L;
        var commentContent = "Added comment";

        commentRepository.addComment(postId, commentContent);

        var foundedComment = commentRepository.findAllCommentsByPostId(postId).stream()
                .filter(c -> commentContent.equals(c.getContent()))
                .findFirst()
                .orElse(null);

        assertNotNull(foundedComment);
    }

    @Test
    void deleteCommentById_shouldDeleteComment() {
        var commentId = 3L;

        commentRepository.deleteCommentById(commentId);

        var count = jdbcTemplate.queryForObject("select count(1) from t_comment where id=" + commentId, Integer.class);

        assertEquals(0, count);
    }

    @Test
    void updateComment_shouldUpdateComment() {
        var commentId = 2L;
        var expected = "Updated comment";

        commentRepository.updateComment(commentId, expected);

        var founded = jdbcTemplate.query("select id, post_id, content from t_comment where id=?",
                new CommentRowMapper(), commentId).get(0);

        assertNotNull(founded);
        assertEquals(commentId, founded.getId());
        assertEquals(expected, founded.getContent());
    }

}
