# AGENTS.md

## Perintah build & verifikasi
- Jalankan hanya `.gradlew.bat` (wrapper Windows; `gradlew` shell tidak jalan di cmd).
- Verifikasi build/kompilasi: `.\gradlew.bat compileDebugKotlin` — jalankan sebelum menyelesaikan kerja.
- `lint` tersedia dari AGP (tidak ada config lint khusus). Tidak ada tooling typecheck/format terpisah.

## Arsitektur & wiring
- Entry: `MainActivity` → `IngatinNav()` di `IngatinApp.kt` (nav graph TIDAK di file terpisah). `SplashViewModel` menentukan start destination: `home` jika sudah login, selain itu `login` (delay 1500ms).
- Alur: Compose → ViewModel → Repository → Firebase, via `StateFlow<UiState>` sealed (Loading/Success/Error/Empty). Jangan push state langsung ke UI.
- DI: modul Hilt hanya di `data/di/FirebaseModule.kt`. Repository `@Inject constructor`, ViewModel `@HiltViewModel`.
- Kode Hilt di-codegen via KSP — hasil build (`build/`, `.gradle/`) jangan dikomit.

## Firebase / Firestore
- `app/google-services.json` WAJIB ada untuk build — jangan dihapus/di-gitignore.
- Skema per user: `users/{uid}/tasks` dan `users/{uid}/categories`.
- ID kategori digenerate klien sebagai `c-%03d` dari hitung dokumen (`TaskRepository.generateIdCategory`) → rentan race/collision; jangan diubah diam-diam tanpa pertimbangan ini.

## Ambang API & notifikasi
- `minSdk 24` tetapi helper timestamp pakai `@RequiresApi(Build.VERSION_CODES.O)` — hati-hati saat menyentuh logika tanggal.
- Class `ReminderWorker` ada di file `NotificationWorker.kt` (nama kelas ≠ nama file). Pengingat dijadwalkan lewat `ui/screen/notif/ShowSimpleNotification.kt`.
- Butuh runtime permission `POST_NOTIFICATIONS` di API 33+.

## Dependensi
- Version catalog `gradle/libs.versions.toml`; beberapa dependensi masih hardcoded di `app/build.gradle.kts` (hilt 2.57.1, work 2.9.0, kotlinx-coroutines-play-services, hilt-navigation-compose). Tambah baru via catalog, jangan hardcode.

## Lainnya
- Commit style: `type(scope) : pesan` (spasi sebelum titik dua).
- Skill agent: `.agents/skills/` (android-cli, edge-to-edge, r8-analyzer, styles).
- Teks dokumentasi/user-facing dalam Bahasa Indonesia.
