# Modul Bacaan & Kuis Event Contract

## Event: `QUIZ_FINISHED`
Dikirim ketika user berhasil menjawab kuis dengan benar.

**Payload:**
```json
{
  "userId": "uuid-dari-user",
  "bacaanId": "uuid-dari-bacaan",
  "status": "SUCCESS"
}