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
* Arsitektur: MVVM (Model - ViewModel - View) dengan prinsip package-by-feature
* Manajemen State: ViewModel + Kotlin StateFlow & collectAsStateWithLifecycle()
* Dependency Injection: Hilt
* Networking: Retrofit & OkHttp
* Navigation: Jetpack Navigation Compose
* Penyimpanan Lokal: DataStore & Room
* Push Notification: Firebase Cloud Messaging (FCM)
* Backend: REST API Laravel (dari proyek [e-Disaster Web](https://github.com/mustafa-fathur/e-disaster-web))

---

## Struktur Proyek

```
app/src/main/java/com/example/e_disaster/
│
├── data/                            # LAYER DATA (MODEL)
│   ├── local/                        # Penyimpanan lokal (Jetpack DataStore & Room later...)
│   ├── model/                        # Data class bersih untuk UI (domain models)
│   ├── remote/                       # Konfigurasi & service Retrofit
│   │   ├── dto/                      # Data Transfer Objects (DTO) untuk response API
│   │   └── service/                  # Interface Retrofit (AuthApiService, dll)
│   └── repository/                   # Repository sebagai Single Source of Truth
│
├── di/                              # DEPENDENCY INJECTION (HILT)
│   ├── AppModule.kt                  # Menyediakan dependensi level aplikasi (Repository, Preferences)
│   └── NetworkModule.kt              # Menyediakan dependensi jaringan (Retrofit, OkHttp)
│
├── notification/                     # Layanan Firebase & handler notifikasi (jika ada)
│
└── ui/                              # LAYER UI (VIEW & VIEWMODEL)
    ├── components/                   # Komponen UI global (TopAppBar, Badge, dll)
    ├── features/                     # Fitur aplikasi
    │   ├── auth/                     #   - Fitur Autentikasi
    │   │   ├── login/
    │   │   └── profile/
    │   ├── disaster/                 #   - Fitur Manajemen Bencana
    │   │   ├── list/
    │   │   └── detail/
    │   └── ... (fitur lainnya seperti home, history)
    │
    ├── navigation/                   # Konfigurasi NavGraph Jetpack Navigation
    └── theme/                        # Tema aplikasi (Color, Typography)

```

---

## Cara Menjalankan Proyek

1. Pastikan Android Studio terbaru sudah terinstal.
2. Clone repositori ini:

   ```bash
   git clone https://github.com/mustafa-fathur/e-disaster.git
   ```
3. Buka proyek di Android Studio.
4. Pastikan koneksi internet aktif dan API Laravel sudah berjalan.
5. Jalankan aplikasi pada emulator atau perangkat fisik.

---

## Konfigurasi API (To be configured)

Ubah `BASE_URL` pada file berikut agar sesuai dengan alamat server Laravel kamu:

```kotlin
// di/NetworkModule.kt
private const val BASE_URL = "https://edisaster.fathur.tech/api/v1"
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