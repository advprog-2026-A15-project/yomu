CREATE TABLE bacaan (
                        id UUID PRIMARY KEY,
                        judul VARCHAR(255) NOT NULL,
                        isi_teks TEXT NOT NULL,
                        kategori VARCHAR(255)
);

CREATE TABLE quiz (
                      id UUID PRIMARY KEY,
                      bacaan_id UUID NOT NULL REFERENCES bacaan(id),
                      pertanyaan TEXT NOT NULL,
                      jawaban_benar VARCHAR(255) NOT NULL
);

CREATE TABLE quiz_attempt (
                              id UUID PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              quiz_id UUID NOT NULL REFERENCES quiz(id),
                              waktu_pengerjaan TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Data 1: Kategori Edukasi
INSERT INTO bacaan (id, judul, isi_teks, kategori)
VALUES ('11111111-1111-1111-1111-111111111111', 'Pentingnya Literasi Digital', 'Literasi digital membantu kita menyaring informasi yang salah atau hoax saat menggunakan internet.', 'Edukasi');

INSERT INTO quiz (id, bacaan_id, pertanyaan, jawaban_benar)
VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Apa yang bisa kita saring dengan literasi digital?', 'hoax');

-- Data 2: Kategori Sejarah
INSERT INTO bacaan (id, judul, isi_teks, kategori)
VALUES ('33333333-3333-3333-3333-333333333333', 'Proklamasi Kemerdekaan', 'Teks Proklamasi Kemerdekaan Indonesia dibacakan pada tanggal 17 Agustus 1945 oleh Ir. Soekarno didampingi Mohammad Hatta.', 'Sejarah');

INSERT INTO quiz (id, bacaan_id, pertanyaan, jawaban_benar)
VALUES ('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', 'Siapa tokoh yang membacakan teks proklamasi?', 'soekarno');

-- Data 3: Kategori Sains
INSERT INTO bacaan (id, judul, isi_teks, kategori)
VALUES ('55555555-5555-5555-5555-555555555555', 'Mengenal Tata Surya', 'Matahari adalah pusat tata surya kita. Bumi merupakan planet ketiga terdekat dari matahari yang memiliki kehidupan.', 'Sains');

INSERT INTO quiz (id, bacaan_id, pertanyaan, jawaban_benar)
VALUES ('66666666-6666-6666-6666-666666666666', '55555555-5555-5555-5555-555555555555', 'Bumi adalah planet keberapa dari matahari?', 'ketiga');