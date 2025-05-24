package ru.yandex.practicum.blog_app.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Comment {
    private Long id;
    private Long postId;
    private String content;
}
