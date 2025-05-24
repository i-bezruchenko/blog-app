package ru.yandex.practicum.blog_app.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Post {
    private Long id;
    private String title;
    private byte[] image;
    private String content;
    private String contentPreview;
    private List<String> contentParts;
    private Integer likesCount;
    private List<Comment> comments;
    private List<String> tags;
    private String tagsAsText;
}
