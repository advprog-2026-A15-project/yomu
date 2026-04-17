CREATE TABLE comment
(
    id           UUID PRIMARY KEY,
    isi_komentar TEXT      NOT NULL,
    bacaan_id    UUID      NOT NULL,
    user_id      BIGINT    NOT NULL,
    created_at   TIMESTAMP NOT NULL,
    parent_id    UUID,
    CONSTRAINT fk_comment_bacaan FOREIGN KEY (bacaan_id) REFERENCES bacaan (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment (id) ON DELETE CASCADE
);