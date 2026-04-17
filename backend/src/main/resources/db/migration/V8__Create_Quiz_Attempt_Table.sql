CREATE TABLE IF NOT EXISTS quiz_attempt (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_id UUID NOT NULL REFERENCES quiz (id),
    waktu_pengerjaan TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

