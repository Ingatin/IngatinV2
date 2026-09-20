<div align="center">

# Ingatin : Pengingat & Manajemen Tugas

![Kotlin](https://img.shields.io/badge/kotlin-%232.2.0-blue?style=flat&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/ui-jetpack%20compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white)
![Minimum SDK](https://img.shields.io/badge/minSdk-24-orange)
![Target SDK](https://img.shields.io/badge/targetSdk-35-brightgreen)
![Firebase Auth](https://img.shields.io/badge/firebase-auth-FFCA28?style=flat&logo=firebase&logoColor=black)
![Firestore](https://img.shields.io/badge/firebase-firestore-FFCA28?style=flat&logo=firebase&logoColor=black)
</div>

---

**Ingatin** adalah aplikasi manajemen tugas Android Native yang bikin kamu nggak pernah lupa deadline — ringan, intuitif, dan siap mengingatkanmu lewat push notification.

### 📖 Detail

- **Aplikasi apa** — Ingatin adalah aplikasi Android manajemen tugas berbasis **Kotlin** dan **Jetpack Compose**.
- **Masalahnya** — Deadline dan tugas sering terlewat karena tidak ada tempat rapi untuk mencatat dan memantaunya.
- **Hasilnya** — Dengan Ingatin, setiap tugas bisa dicatat dengan cepat, diberi kategori, dan deadline-nya diingatkan lewat push notification. Jadi kamu lebih fokus mengerjakan tugas, tanpa khawatir lupa tenggat waktu.

---

## ✨ Fitur Utama

- **🔐 Autentikasi** – Daftar, masuk, dan keluar akun dengan validasi input, serta deteksi sesi otomatis di layar splash.
- **📋 Manajemen Tugas** – Buat, lihat, edit, dan hapus tugas dengan deadline (tanggal & waktu), kategori custom, serta penyaringan per kategori.
- **🔔 Push Notification** – Pengingat deadline otomatis yang dikirim melalui **WorkManager**.

---

## 🛠 Tech Stack

| Layer            | Teknologi                       |
|------------------|---------------------------------|
| Bahasa           | Kotlin                          |
| UI Framework     | Jetpack Compose (Material 3)    |
| Arsitektur       | MVVM                            |
| State Management | StateFlow + `UiState`           |
| DI               | Hilt                            |
| Data             | Firebase Auth & Cloud Firestore |
| Navigasi         | Navigation Compose              |
| Background       | WorkManager                     |
| Lainnya          | SplashScreen API, KSP           |

---

## 🏗 Arsitektur

Ingatin mengikuti pola **MVVM** (Model-View-ViewModel) dengan aliran data satu arah. Setiap layar mengambil state dari `ViewModel`-nya melalui `StateFlow` dan menampilkan `UiState` bertipe sealed class (`Loading`, `Success`, `Error`, `Empty`), sehingga UI hanya menjadi fungsi dari state.

```
Layar Compose  →  ViewModel (StateFlow/UiState)  →  Repository  →  Firebase
```

### Struktur Proyek

```
app/src/main/java/id/co/ingatin/
├── data/
│   ├── di/           # Modul Hilt (Firebase)
│   ├── model/        # DTO & model domain + mapper
│   ├── repository/   # Repository Auth & Task
│   └── utils/        # Helper (format timestamp)
└── ui/
    ├── common/       # UiState bersama
    ├── components/   # Composable yang dapat digunakan ulang
    ├── screen/       # Layar auth / home / task / notif
    └── theme/        # Color, Type, Theme
```

---

## 🤝 Kontribusi

Ingatin saat ini merupakan proyek pribadi, tetapi kontribusi sangat kami sambut! Jika kamu ingin membantu:

1. **Fork** repositori ini.
2. Buat **branch** fitur/perbaikan (mis. `feat/fitur-keren` atau `fix/perbaikan-bug`).
3. Lakukan perubahan dan commit.
4. Ajukan **Pull Request** yang menjelaskan apa yang kamu lakukan dan alasannya.

Baik itu memperbaiki bug, menambah fitur, atau menyempurnakan dokumentasi — setiap bantuan sangat berarti!

---
