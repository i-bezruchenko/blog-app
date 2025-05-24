package ru.yandex.practicum.blog_app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.blog_app.util.PostRowMapper;
import ru.yandex.practicum.blog_app.consts.SQL;
import ru.yandex.practicum.blog_app.model.Post;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Post> findAll(int pageNumber, int pageSize) {
        var offset = pageSize * (pageNumber - 1);
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("offsetValue", offset)
            .addValue("limitValue", pageSize);
        return jdbcTemplate.query(SQL.GET_ALL_POSTS, params, new PostRowMapper());
    }

    public Long savePost(String title, byte[] image, List<String> tags, String content) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource postParams = new MapSqlParameterSource()
                .addValue("title", title)
                .addValue("image", image)
                .addValue("content", content);

        jdbcTemplate.update(
                "INSERT INTO t_post (title, image, content, likes_count) " +
                        "VALUES (:title, :image, :content, 0)",
                postParams,
                keyHolder,
                new String[]{"id"}
        );

        Long postId = keyHolder.getKey().longValue();

        if (tags != null && !tags.isEmpty()) {
            List<MapSqlParameterSource> tagParams = tags.stream()
                    .distinct()
                    .map(tag -> new MapSqlParameterSource("name", tag))
                    .toList();

            jdbcTemplate.batchUpdate(
                    "INSERT INTO t_tag (name) VALUES (:name) ON CONFLICT (name) DO NOTHING",
                    tagParams.toArray(new MapSqlParameterSource[0])
            );

            Map<String, Long> tagIdMap = jdbcTemplate.query(
                            "SELECT id, name FROM t_tag WHERE name IN (:tags)",
                            new MapSqlParameterSource("tags", tags),
                            (rs, rowNum) -> {
                                Map<String, Long> result = new HashMap<>();
                                result.put(rs.getString("name"), rs.getLong("id"));
                                return result;
                            })
                    .stream()
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            List<MapSqlParameterSource> postTagParams = tags.stream()
                    .map(tag -> new MapSqlParameterSource()
                            .addValue("post_id", postId)
                            .addValue("tag_id", tagIdMap.get(tag)))
                    .toList();

            jdbcTemplate.batchUpdate(
                    "INSERT INTO t_post_tag (post_id, tag_id) VALUES (:post_id, :tag_id) " +
                            "ON CONFLICT DO NOTHING",
                    postTagParams.toArray(new MapSqlParameterSource[0])
            );
        }

        return postId;
    }

    public void updatePost(Post post) {
        MapSqlParameterSource postParams = new MapSqlParameterSource()
                .addValue("id", post.getId())
                .addValue("title", post.getTitle())
                .addValue("image", post.getImage())
                .addValue("content", post.getContent())
                .addValue("likesCount", post.getLikesCount());

        jdbcTemplate.update(
                """
                        UPDATE t_post SET
                        title = :title,
                        image = :image,
                        content = :content,
                        likes_count = :likesCount
                        WHERE id = :id
                        """,
                postParams
        );

        updatePostTags(post.getId(), post.getTags());
    }

    private void updatePostTags(Long postId, List<String> newTags) {
        if (newTags == null) {
            newTags = Collections.emptyList();
        }

        jdbcTemplate.update(
                "DELETE FROM t_post_tag WHERE post_id = :postId",
                new MapSqlParameterSource("postId", postId)
        );

        if (!newTags.isEmpty()) {
            List<MapSqlParameterSource> tagParams = newTags.stream()
                    .distinct()
                    .map(tag -> new MapSqlParameterSource("name", tag))
                    .toList();

            jdbcTemplate.batchUpdate(
                    "INSERT INTO t_tag (name) VALUES (:name) ON CONFLICT (name) DO NOTHING",
                    tagParams.toArray(new MapSqlParameterSource[0])
            );

            Map<String, Long> tagIdMap = jdbcTemplate.query(
                            "SELECT id, name FROM t_tag WHERE name IN (:tags)",
                            new MapSqlParameterSource("tags", newTags),
                            (rs, rowNum) -> {
                                Map<String, Long> result = new HashMap<>();
                                result.put(rs.getString("name"), rs.getLong("id"));
                                return result;
                            })
                    .stream()
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            List<MapSqlParameterSource> postTagParams = newTags.stream()
                    .map(tag -> new MapSqlParameterSource()
                            .addValue("postId", postId)
                            .addValue("tagId", tagIdMap.get(tag)))
                    .toList();

            jdbcTemplate.batchUpdate(
                    "INSERT INTO t_post_tag (post_id, tag_id) VALUES (:postId, :tagId)",
                    postTagParams.toArray(new MapSqlParameterSource[0])
            );
        }
    }

    public List<Post> findPostsByTags(List<String> tagNames, int pageNumber, int pageSize) {
        var offset = pageSize * (pageNumber - 1);
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("tags", tagNames)
            .addValue("tagCount", tagNames.size())
            .addValue("offsetValue", offset)
            .addValue("limitValue", pageSize);

        return jdbcTemplate.query(SQL.GET_POSTS_BY_TAGS, params, new PostRowMapper());
    }

    public Post findPostById(Long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("postId", postId);
        return jdbcTemplate.queryForObject(SQL.GET_POST_BY_ID, params, new PostRowMapper());
    }

    public void delete(Long postId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("postId", postId);
        jdbcTemplate.update(SQL.DELETE_POST_TAG_ROWS, params);
        jdbcTemplate.update(SQL.DELETE_POST_BY_ID, params);
    }
}
