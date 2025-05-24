package ru.yandex.practicum.blog_app.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.blog_app.config.DataSourceConfig;
import ru.yandex.practicum.blog_app.model.Post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(DataSourceConfig.class)
class PostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

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
    }

    @Test
    void findAll_shouldReturnAllPostsByPage() {
        var expected = getExpectedPosts();

        var founded = postRepository.findAll( 1, 5);

        assertNotNull(founded);
        assertEquals(2, founded.size());
        assertArrayEquals(expected.toArray(), founded.toArray());
    }

    @Test
    void findAll_shouldReturnAllPostsByTag() {
        var expected = getExpectedPosts();

        var founded = postRepository.findPostsByTags(List.of("Tag1"), 1, 5);

        assertNotNull(founded);
        assertEquals(1, founded.size());
        assertEquals(expected.get(0), founded.get(0));
    }

    @Test
    void savePost_shouldAddPostToDb() {
        var title = "Added Post Title";
        var image = "Added Post Image".getBytes();
        var tags = List.of("added post");
        var tagsAsText = "added post";
        var content = "Added Post Text";

        var postId = postRepository.savePost(title, image, tags, content);

        var founded = postRepository.findPostById(postId);

        assertNotNull(founded);
        assertEquals(title, founded.getTitle());
        assertArrayEquals(image, founded.getImage());
        assertEquals(tagsAsText, founded.getTagsAsText());
        assertEquals(content, founded.getContent());
        assertEquals(0, founded.getLikesCount());
    }

    @Test
    void updatePost_shouldUpdatePostInDb() {
        var postId = 1L;
        var title = "Updated Post Title";
        var image = "Updated Post Image".getBytes();
        var content = "updated Post Text";
        var likesCount = 100;
        Post post = new Post();
        post.setId(postId);
        post.setTitle(title);
        post.setImage(image);
        post.setContent(content);
        post.setLikesCount(likesCount);
        post.setContentPreview(content);
        post.setContentParts(List.of(content));

        postRepository.updatePost(post);

        var founded = postRepository.findPostById(postId);

        assertNotNull(founded);
        assertEquals(post, founded);
    }

    @Test
    void delete_shouldDeletePostFromDb() {
        var postId = 1L;

        postRepository.delete(postId);

        Assertions.assertThrows(Exception.class, () -> postRepository.findPostById(postId));
    }

    private List<Post> getExpectedPosts(){
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setImage("Post Image Content One".getBytes());
        post1.setContent("Post Text1");
        post1.setLikesCount(10);
        post1.setContentPreview("Post Text1");
        post1.setContentParts(List.of("Post Text1"));
        post1.setTags(List.of("Tag1", "Tag2"));
        post1.setTagsAsText("Tag1 Tag2");
        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setImage("Post Image Content Two".getBytes());
        post2.setContent("Post Text2");
        post2.setLikesCount(5);
        post2.setContentPreview("Post Text2");
        post2.setContentParts(List.of("Post Text2"));
        post2.setTags(List.of("Tag2", "Tag3"));
        post2.setTagsAsText("Tag2 Tag3");
        return List.of(post1, post2);
    }
}
