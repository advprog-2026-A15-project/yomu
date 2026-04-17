CREATE TABLE bacaan
(
    id       UUID PRIMARY KEY,
    judul    VARCHAR(255) NOT NULL,
    isi_teks TEXT         NOT NULL
);

CREATE TABLE quiz
(
    id            UUID PRIMARY KEY,
    bacaan_id     UUID         NOT NULL REFERENCES bacaan (id),
    pertanyaan    TEXT         NOT NULL,
    jawaban_benar VARCHAR(255) NOT NULL
);

INSERT INTO bacaan (id, judul, isi_teks)
VALUES ('11111111-1111-1111-1111-111111111111', 'Pentingnya Literasi Digital',
        'Literasi digital membantu kita menyaring informasi yang salah atau hoax.');

INSERT INTO quiz (id, bacaan_id, pertanyaan, jawaban_benar)
VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111',
        'Apa yang bisa kita saring dengan literasi digital?', 'hoax');