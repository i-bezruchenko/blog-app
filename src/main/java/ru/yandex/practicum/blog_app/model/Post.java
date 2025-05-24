package ru.yandex.practicum.blog_app.model;

import java.util.List;

public class Post {
    private Long id;
    private String title;
    private byte[] image;
    private String content;
    private Integer likesCount;
    private List<Comment> comments;
    private List<Tag> tags;

}
