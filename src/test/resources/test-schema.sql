DROP TABLE IF EXISTS t_comment CASCADE;
DROP TABLE IF EXISTS t_post_tag CASCADE;
DROP TABLE IF EXISTS t_tag CASCADE;
DROP TABLE IF EXISTS t_post CASCADE;

CREATE TABLE t_post (
                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        image BYTEA,
                        content TEXT NOT NULL,
                        likes_count INTEGER DEFAULT 0
);

CREATE TABLE t_tag (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE t_post_tag (
                            post_id BIGINT REFERENCES t_post(id),
                            tag_id BIGINT REFERENCES t_tag(id),
                            PRIMARY KEY (post_id, tag_id)
);

CREATE TABLE t_comment (
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           post_id BIGINT REFERENCES t_post(id),
                           content TEXT NOT NULL
);