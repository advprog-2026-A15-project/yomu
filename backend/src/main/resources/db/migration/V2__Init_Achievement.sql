CREATE TABLE IF NOT EXISTS achievement
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL UNIQUE,
    description VARCHAR
(
    255
)
    );

CREATE TABLE IF NOT EXISTS user_achievement
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL,
    achievement_id
    BIGINT
    NOT
    NULL,
    achieved_at
    TIMESTAMP,
    CONSTRAINT
    uq_user_achievement
    UNIQUE
(
    user_id,
    achievement_id
)
    );

INSERT INTO achievement (name, description)
SELECT 'First Read',
       'Selesaikan kuis pertama kali.' WHERE NOT EXISTS (
    SELECT 1 FROM achievement WHERE name = 'First Read'
);
