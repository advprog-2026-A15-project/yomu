UPDATE bacaan
SET isi_teks = 'Literasi digital membantu kita menyaring informasi yang salah atau hoax saat menggunakan internet.',
    kategori = 'Edukasi'
WHERE id = '11111111-1111-1111-1111-111111111111';

INSERT INTO bacaan (id, judul, isi_teks, kategori)
SELECT '33333333-3333-3333-3333-333333333333',
       'Proklamasi Kemerdekaan',
       'Teks Proklamasi Kemerdekaan Indonesia dibacakan pada tanggal 17 Agustus 1945 oleh Ir. Soekarno didampingi Mohammad Hatta.',
       'Sejarah'
WHERE NOT EXISTS (
    SELECT 1
    FROM bacaan
    WHERE id = '33333333-3333-3333-3333-333333333333'
);

INSERT INTO bacaan (id, judul, isi_teks, kategori)
SELECT '55555555-5555-5555-5555-555555555555',
       'Mengenal Tata Surya',
       'Matahari adalah pusat tata surya kita. Bumi merupakan planet ketiga terdekat dari matahari yang memiliki kehidupan.',
       'Sains'
WHERE NOT EXISTS (
    SELECT 1
    FROM bacaan
    WHERE id = '55555555-5555-5555-5555-555555555555'
);

INSERT INTO quiz (id, bacaan_id, pertanyaan, jawaban_benar)
SELECT '44444444-4444-4444-4444-444444444444',
       '33333333-3333-3333-3333-333333333333',
       'Siapa tokoh yang membacakan teks proklamasi?',
       'soekarno'
WHERE NOT EXISTS (
    SELECT 1
    FROM quiz
    WHERE id = '44444444-4444-4444-4444-444444444444'
);

INSERT INTO quiz (id, bacaan_id, pertanyaan, jawaban_benar)
SELECT '66666666-6666-6666-6666-666666666666',
       '55555555-5555-5555-5555-555555555555',
       'Bumi adalah planet keberapa dari matahari?',
       'ketiga'
WHERE NOT EXISTS (
    SELECT 1
    FROM quiz
    WHERE id = '66666666-6666-6666-6666-666666666666'
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_ua_user'
    ) THEN
        ALTER TABLE user_achievement
            ADD CONSTRAINT fk_ua_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_cg_owner'
    ) THEN
        ALTER TABLE clan_group
            ADD CONSTRAINT fk_cg_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_cm_clan'
    ) THEN
        ALTER TABLE clan_member
            ADD CONSTRAINT fk_cm_clan FOREIGN KEY (clan_id) REFERENCES clan_group (id) ON DELETE CASCADE;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_cm_user'
    ) THEN
        ALTER TABLE clan_member
            ADD CONSTRAINT fk_cm_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
    END IF;
END
$$;
