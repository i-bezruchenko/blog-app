package ru.yandex.practicum.blog_app.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SQL {
    public static final String GET_POST_BY_ID = """
            SELECT 
                p.id,
                p.title,
                p.image,
                p.content,
                p.likes_count AS likesCount,
                (
                    SELECT array_agg(t.name)
                    FROM t_tag t
                    JOIN t_post_tag pt ON t.id = pt.tag_id
                    WHERE pt.post_id = p.id
                ) AS tags
            FROM 
                t_post p
            WHERE 
                p.id = :postId
            """;

    public static final String GET_POSTS_BY_TAGS = """
        SELECT 
            p.id,
            p.title,
            p.image,
            p.content,
            p.likes_count AS likesCount,
            (
                SELECT array_agg(t.name)
                FROM t_tag t
                JOIN t_post_tag pt ON t.id = pt.tag_id
                WHERE pt.post_id = p.id
            ) AS tags
        FROM 
            t_post p
        WHERE 
            p.id IN (
                SELECT pt.post_id
                FROM t_post_tag pt
                JOIN t_tag t ON pt.tag_id = t.id
                WHERE t.name IN (:tags)
                GROUP BY pt.post_id
                HAVING COUNT(DISTINCT t.name) = :tagCount
            )
        limit :limitValue offset :offsetValue
        """;

    public static final String GET_ALL_POSTS = """
            SELECT 
                p.id,
                p.title,
                p.image,
                p.content,
                p.likes_count AS likesCount,
                (
                    SELECT array_agg(t.name)
                    FROM t_tag t
                    JOIN t_post_tag pt ON t.id = pt.tag_id
                    WHERE pt.post_id = p.id
                ) AS tags
            FROM 
                t_post p
            limit :limitValue offset :offsetValue
            """;

    public static final String DELETE_POST_BY_ID = """
            delete from t_post where id = :postId
            """;

    public static final String DELETE_POST_TAG_ROWS = """
            delete from t_post_tag where post_id = :postId
            """;

    public static final String GET_COMMENTS_BY_POST_ID = """
            select id, post_id, content from t_comment where post_id = :postId
            """;

    public static final String DELETE_COMMENTS_BY_POST_ID = """
            delete from t_comment where post_id = :postId
            """;

    public static final String DELETE_COMMENT_BY_ID = """
            delete from t_comment where id = :commentId
            """;

    public static final String ADD_COMMENT = """
            insert into t_comment (post_id, content) values (:postId, :contentValue)
            """;

    public static final String UPDATE_COMMENT = """
            update t_comment set content = :contentValue where id = :commentId
            """;
}
