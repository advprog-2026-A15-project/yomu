## 👥 Anggota Kelompok A15
- M. Adella Fathir Supriadi (2406495640)
- Ali Akbar Murthadha (2406495754)
- Christna Yosua Rotinsulu (2406495691)
- Nathanael Leander Herdanatra (2406421320)
- Tirta Rendy Siahaan (2406355621)


## 🌐 Deployment Link

[http://3.229.117.61/](http://3.229.117.61/)

## 🖥️ Cara Setup Aplikasi

- Buka folder `backend` di IntelliJ IDEA Ultimate.
- Lakukan setup Google OAuth2 Login sesuai docs di bawah.
- Copy [`yomu/backend/src/main/resources/application.properties.example`](../backend/src/main/resources/application.properties.example) ke `yomu/backend/src/main/resources/application.properties`.
- Isi [YOMU SECRET] dengan token JWT random yang bisa diperoleh [di sini.](https://randomkeygen.com/jwt-secret)
- Isi [CLIENT ID] dan [CLIENT SECRET] dengan nilai yang diperoleh setelah setup Google OAuth2.
- Copy [`frontend/.env.example`](../frontend/.env.example) di folder frontend ke `frontend/.env.local`.
- Ubah `VITE_GOOGLE_CLIENT_ID` di`.env.local` menjadi [CLIENT ID] sebelumnya.
- Jalankan/run BackendApplication (tombol segitiga hijau).
- Buka folder `frontend` di terminal.
- Pastikan terinstall NodeJS v24.13.1.
- Jalankan `npm install` lalu `npm run dev`
- Buka alamat localhost yg keluar di browser

# Docs Group Preparation
Link : <a href="https://docs.google.com/document/d/1qXx9EYBr9EgFPy5gCOpMnVIi1kwDC42dGiQXf2kc2eI/edit?usp=sharing">Prep Group A15</a>

# Integrasi Google OAuth2 Login
Google OAuth2 memungkinkan pengguna login menggunakan akun Google tanpa perlu membuat akun baru di aplikasi.
Google Cloud Console:  
https://console.cloud.google.com/

---

# 1. Prasyarat

Pastikan beberapa hal berikut sudah tersedia:

- Akun Google
- Project **Spring Boot**
- Dependency OAuth2 pada project

### Dependency (Gradle)

```gradle
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
```

Dependency ini digunakan untuk mengaktifkan fitur **OAuth2 Client** pada Spring Security.

---

# 2. Membuat Project di Google Cloud Console

1. Buka halaman berikut:

```
https://console.cloud.google.com/
```

2. Klik **Select Project**

3. Klik **New Project**

4. Isi informasi project:

- Project Name
- Organization (optional)

5. Klik **Create**

Setelah project berhasil dibuat, pilih project tersebut.

---

# 3. Konfigurasi OAuth Consent Screen

1. Masuk ke menu:

```
APIs & Services → OAuth consent screen
```

2. Pilih tipe aplikasi:

```
External
```

3. Isi informasi aplikasi:

- App Name
- User Support Email
- Developer Contact Email

4. Tambahkan **Test Users** jika aplikasi masih dalam tahap development.

OAuth Consent Screen digunakan untuk menampilkan halaman izin ketika pengguna login menggunakan Google.

---

# 4. Membuat OAuth Client ID

Masuk ke menu:

```
APIs & Services → Credentials
```

Langkah selanjutnya:

1. Klik **Create Credentials**
2. Pilih **OAuth client ID**
3. Pilih tipe aplikasi:

```
Web Application
```

4. Isi konfigurasi berikut.

### Authorized JavaScript Origins

```
http://localhost:8080
```

### Authorized Redirect URIs

```
http://localhost:8080/login/oauth2/code/google
```

Redirect URI adalah endpoint yang akan menerima respons dari Google setelah pengguna berhasil login.

5. Klik **Create**

Setelah selesai, Google akan memberikan:

```
Client ID
Client Secret
```

Simpan kedua nilai tersebut.

---


# 6. Alur OAuth2 Login

Alur autentikasi Google OAuth2 adalah sebagai berikut:

1. User klik **Login with Google**
2. User diarahkan ke **Google Authorization Server**
3. User login dan memberikan izin akses
4. Google mengirimkan **authorization code**
5. Spring Boot menukar authorization code dengan **access token**
6. Informasi user dikirim kembali ke aplikasi

Diagram sederhana flow OAuth2:

```
User → Google Authorization Server → Access Token → Application
```

---

# 7. Endpoint Default Spring Security

Spring Boot menyediakan endpoint OAuth2 secara otomatis.

### Endpoint Login

```
/oauth2/authorization/google
```

Endpoint ini digunakan untuk memulai proses login dengan Google.

### Redirect Endpoint

```
/login/oauth2/code/google
```

Endpoint ini digunakan untuk menerima respons dari Google setelah autentikasi berhasil.

---

# 8. Testing Login

Jalankan aplikasi Spring Boot:

```bash
./gradlew bootRun
```

Buka browser dan akses:

```
http://localhost:8080/oauth2/authorization/google
```

Jika konfigurasi berhasil, halaman login Google akan muncul.

---

# 9. Troubleshooting

### redirect_uri_mismatch

Pastikan URI berikut sama persis antara Google Console dan aplikasi.

```
http://localhost:8080/login/oauth2/code/google
```

---

### access_denied

Tambahkan email pengguna ke daftar **Test Users** pada OAuth Consent Screen.

---

# 10. Referensi

Google Cloud Console  
https://console.cloud.google.com/

Google OAuth2 Documentation  
https://developers.google.com/identity/protocols/oauth2

Spring Security OAuth2  
https://docs.spring.io/spring-security/reference/servlet/oauth2/login

