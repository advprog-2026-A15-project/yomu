#!/bin/bash

# Hentikan eksekusi jika ada perintah yang gagal
set -e

echo "=== Memulai Proses Deployment Yomu ==="

# Pindah ke direktori tempat JAR berada
cd /home/ubuntu/yomu-deploy/backend/build/libs

# Menyuntikkan Secrets ke file .env
echo "Menulis environment variables..."
echo "DB_URL=$DB_URL" > .env
echo "DB_USERNAME=$DB_USERNAME" >> .env
echo "DB_PASSWORD=$DB_PASSWORD" >> .env
echo "JWT_SECRET=$JWT_SECRET" >> .env
echo "GOOGLE_CLIENT_ID=$GOOGLE_CLIENT_ID" >> .env
echo "GOOGLE_CLIENT_SECRET=$GOOGLE_CLIENT_SECRET" >> .env

echo "Menghentikan backend lama jika ada..."
pkill -f 'java -jar' || true
sleep 3 # Beri waktu agar port 8080 benar-benar tertutup

echo "Membersihkan file jar yang tidak perlu..."
rm -f *-plain.jar

echo "Menyalakan backend baru..."
# Menggunakan nohup dan mengarahkan semua I/O agar proses benar-benar lepas
nohup java -jar *.jar > /home/ubuntu/yomu-backend.log 2>&1 < /dev/null &

echo "Memperbarui frontend..."
sudo rm -rf /var/www/html/*
sudo cp -r /home/ubuntu/yomu-deploy/frontend/dist/* /var/www/html/
sudo systemctl restart nginx

echo "=== Deployment Selesai Sukses! ==="