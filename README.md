# e-Disaster Mobile (Android)

Aplikasi **e-Disaster** adalah aplikasi Android yang dikembangkan menggunakan **Kotlin** dan **Jetpack Compose**.
Tujuan utama aplikasi ini adalah membantu petugas dan relawan dalam melaporkan dan mengelola informasi terkait bencana secara langsung di lapangan.

---

## Tujuan Proyek

Aplikasi ini merupakan bagian dari sistem **e-Disaster**, di mana:

* Admin mengelola data melalui web (Laravel + Livewire).
* Petugas & Relawan menggunakan aplikasi mobile untuk:

  * Melihat daftar bencana yang terjadi.
  * Membuat laporan lapangan.
  * Menambahkan data korban dan bantuan.
  * Menerima notifikasi atau update bencana terbaru secara real-time melalui Firebase Cloud Messaging (FCM).

---

## Teknologi yang Digunakan

* Bahasa Pemrograman: Kotlin
* UI Framework: Jetpack Compose
* Arsitektur: MVVM (Model - ViewModel - View)
* Networking: Retrofit
* State Management: ViewModel + StateFlow
* Navigation: Jetpack Navigation Compose
* Push Notification: Firebase Cloud Messaging (FCM)
* Backend: REST API Laravel (dari proyek [e-Disaster Web](https://github.com/mustafa-fathur/e-disaster-web))

---

## Struktur Proyek

```
app/
 └── src/main/java/com/example/edisaster/
      ├── data/
      │   ├── model/         # Data class untuk API & response
      │   ├── remote/        # Retrofit API service
      │   └── repository/    # Repository untuk koneksi data
      │
      ├── ui/
      │   ├── screen/        # UI tiap fitur (Login, Dashboard, dsb)
      │   ├── viewmodel/     # ViewModel tiap fitur
      │   ├── components/    # Komponen UI umum (Button, Card)
      │   ├── navigation/    # Navigasi antar layar
      │   └── theme/         # Warna dan typography
      │
      ├── notification/      # Layanan Firebase & handler notifikasi
      ├── utils/             # Const & helper
      └── MainActivity.kt    # Entry point aplikasi
```

---

## Cara Menjalankan Proyek

1. Pastikan Android Studio terbaru sudah terinstal.
2. Clone repositori ini:

   ```bash
   git clone https://github.com/mustafa-fathur/e-disaster-android.git
   ```
3. Buka proyek di Android Studio.
4. Pastikan koneksi internet aktif dan API Laravel sudah berjalan.
5. Jalankan aplikasi pada emulator atau perangkat fisik.

---

## Konfigurasi API (To be configured)

Ubah `BASE_URL` pada file berikut agar sesuai dengan alamat server Laravel kamu:

```kotlin
// data/remote/ApiClient.kt
private const val BASE_URL = "https://edisaster.siunand.my.id/api/"
```

---

## Konfigurasi Firebase Cloud Messaging (FCM)

Aplikasi menggunakan **Firebase Cloud Messaging (FCM)** untuk menerima notifikasi real-time dari backend Laravel.
Laravel akan mengirim notifikasi ke pengguna mobile untuk beberapa kondisi, seperti:

* Bencana baru terdeteksi dari **BMKG API** dan Bencana yang diinput secara manual.
* Status bencana diperbarui.
* Laporan lapangan baru ditambahkan.
* Relawan disetujui atau ditolak oleh admin.

Token FCM pengguna akan disimpan di database Laravel untuk mengirim notifikasi sesuai target.

---

## Peran Pengguna

| Jenis Pengguna | Akses                                                                 |
| -------------- | --------------------------------------------------------------------- |
| **Admin**      | Hanya di web (Laravel)                                                |
| **Petugas**    | Akses penuh ke aplikasi mobile                                        |
| **Relawan**    | Akses penuh ke aplikasi mobile (setelah disetujui admin / registrasi) |

---

## Arsitektur Singkat (MVVM)

```
UI (Jetpack Compose)
    ↓
ViewModel (mengatur state dan logika)
    ↓
Repository (mengambil data dari API)
    ↓
Laravel REST API
    ↓
Firebase Cloud Messaging (Notifikasi Real-time)
```

---

## Lisensi

Proyek ini dikembangkan untuk keperluan tugas besar mata kuliah Pemrograman Teknologi Bergerak (PTB) / Mobile Programming di Program Studi S1 Sistem Informasi Universitas Andalas.
Proyek ini dibuat untuk keperluan akademik dan pembelajaran, bukan untuk tujuan komersial.