CREATE TYPE MEDIATYPE AS ENUM (
    'GAME',
    'MOVIE',
    'SHOW'
);

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(255) NOT NULL UNIQUE,
    alias         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE genres (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE media_entries (
    id           BIGSERIAL PRIMARY KEY,
    author_id    BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    type         MEDIATYPE    NOT NULL,
    release_year INTEGER      NOT NULL,
    minimum_age  INTEGER      NOT NULL
);

CREATE TABLE ratings (
    id             BIGSERIAL PRIMARY KEY,
    author_id      BIGINT    NOT NULL,
    media_entry_id BIGINT    NOT NULL,
    stars          INTEGER   NOT NULL,
    text           TEXT,
    timestamp      TIMESTAMP NOT NULL,
    hidden         BOOLEAN   NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_ratings_stars
        CHECK (stars BETWEEN 1 AND 5)
);

CREATE TABLE media_entry_genres (
    media_entry_id BIGINT NOT NULL,
    genre_id       BIGINT NOT NULL,

    PRIMARY KEY (media_entry_id, genre_id)
);

CREATE TABLE media_entry_favourites (
    media_entry_id BIGINT NOT NULL,
    user_id        BIGINT NOT NULL,

    PRIMARY KEY (media_entry_id, user_id)
);

CREATE TABLE rating_likes (
    rating_id BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,

    PRIMARY KEY (rating_id, user_id)
);

ALTER TABLE media_entries
    ADD CONSTRAINT fk_media_entries_author
        FOREIGN KEY (author_id)
            REFERENCES users (id);

ALTER TABLE ratings
    ADD CONSTRAINT fk_ratings_author
        FOREIGN KEY (author_id)
            REFERENCES users (id);

ALTER TABLE ratings
    ADD CONSTRAINT fk_ratings_media_entry
        FOREIGN KEY (media_entry_id)
            REFERENCES media_entries (id);

ALTER TABLE media_entry_genres
    ADD CONSTRAINT fk_media_entry_genres_media_entry
        FOREIGN KEY (media_entry_id)
            REFERENCES media_entries (id)
            ON DELETE CASCADE;

ALTER TABLE media_entry_genres
    ADD CONSTRAINT fk_media_entry_genres_genre
        FOREIGN KEY (genre_id)
            REFERENCES genres (id)
            ON DELETE CASCADE;

ALTER TABLE media_entry_favourites
    ADD CONSTRAINT fk_media_entry_favourites_media_entry
        FOREIGN KEY (media_entry_id)
            REFERENCES media_entries (id)
            ON DELETE CASCADE;

ALTER TABLE media_entry_favourites
    ADD CONSTRAINT fk_media_entry_favourites_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE rating_likes
    ADD CONSTRAINT fk_rating_likes_rating
        FOREIGN KEY (rating_id)
            REFERENCES ratings (id)
            ON DELETE CASCADE;

ALTER TABLE rating_likes
    ADD CONSTRAINT fk_rating_likes_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;