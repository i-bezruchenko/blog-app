package ru.yandex.practicum.blog_app.util;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.blog_app.model.Post;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class PostRowMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setImage(rs.getBytes("image"));
        String content = rs.getString("content");
        post.setContent(content);
        post.setContentPreview(content != null && content.length() > 300 ? content.substring(0, 300) + "..." : content);
        post.setContentParts(content == null ? List.of() : Arrays.stream(content.split("\\n")).toList());
        post.setLikesCount(rs.getInt("likesCount"));

        Array tagsArray = rs.getArray("tags");
        if (tagsArray != null) {
            Object[] tags = (Object[]) tagsArray.getArray();
            String[] stringTags = Arrays.stream(tags)
                    .map(Object::toString)
                    .toArray(String[]::new);

            post.setTags(Arrays.asList(stringTags));
            post.setTagsAsText(String.join(" ", stringTags));
        }

        return post;
    }
}
