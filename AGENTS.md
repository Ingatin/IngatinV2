# AGENTS.md

## Perintah build & verifikasi
- Verifikasi kompilasi (jalankan sebelum menyelesaikan kerja): `.\gradlew :app:compileDebugKotlin`
  dari root repo. Di shell agent (PowerShell) `.\gradlew` berfungsi; `.\gradlew.bat` juga ada untuk cmd.
- Build + unit test: `.\gradlew :app:assembleDebug :app:testDebugUnitTest`.
- `lint` tersedia dari AGP (tanpa config khusus). Tidak ada tooling typecheck/format terpisah.
- Kalau `processDebugResources` gagal dengan `Couldn't delete ... R.jar`, itu file-lock daemon Kotlin yang transien — cukup jalankan ulang, bukan error kode.

## Arsitektur & wiring
- Entry: `MainActivity` → `IngatinNav()` di `IngatinApp.kt` (nav graph TIDAK di file terpisah). `object Routes` juga di `IngatinApp.kt` — tambah route baru di sana. `SplashViewModel` menentukan start destination: `home` jika sudah login, selain itu `login` (delay 1500ms).
- Alur: Compose → ViewModel → Repository → Firebase, via `StateFlow<UiState>` sealed (Loading/Success/Error/Empty). Jangan push state langsung ke UI.
- DI: modul Hilt di `data/di/` (`FirebaseModule.kt`, `NotificationModule.kt`). Repository `@Inject constructor`, ViewModel `@HiltViewModel`.
- Kode Hilt di-codegen via KSP (`ksp(...)`, bukan kapt) — hasil build (`build/`, `.gradle/`) jangan dikomit.

## Firebase / Firestore
- `app/google-services.json` WAJIB ada untuk build — jangan dihapus/di-gitignore.
- Skema per user: `users/{uid}/tasks` dan `users/{uid}/categories`.
- ID kategori digenerate klien sebagai `c-%03d` dari hitung dokumen (`TaskRepository.generateIdCategory`) → rentan race/collision; jangan diubah diam-diam tanpa pertimbangan ini.
- `TaskRepository.createTasks` wajib mengembalikan `docRef.id` asli (bukan string pesan) — ID itu dipakai `TaskViewModel` untuk arm pengingat.

## Ambang API & notifikasi
- `minSdk 26` tetapi helper timestamp pakai `@RequiresApi(Build.VERSION_CODES.O)` — hati-hati saat menyentuh logika tanggal.
- Pengingat ada di layer `platform/notification/` (AlarmManager, BUKAN WorkManager — README salah soal ini). Hanya `TaskViewModel` yang boleh menyentuh `ReminderScheduler` (via Hilt); layar/Composable lain dilarang mengimpor `platform/` langsung.
- Receiver didaftarkan di `AndroidManifest.xml` sebagai `.platform.notification.ReminderReceiver` / `.BootReceiver` — WAJIB sinkron dengan package.
- `BootReceiver` merakit `ReminderLedger` + `AlarmReminderSchedulerImpl` manual — `BroadcastReceiver` tidak bisa di-inject Hilt tanpa `@AndroidEntryPoint`.
- Butuh runtime permission `POST_NOTIFICATIONS` di API 33+.

## Dependensi
- Version catalog `gradle/libs.versions.toml`. Hardcoded di `app/build.gradle.kts`: `hilt-android` + `hilt-android-compiler` (2.57.1 — versinya sudah ada di catalog, artifact-nya belum), `hilt-navigation-compose`, `kotlinx-coroutines-play-services`. Tambah baru via catalog, jangan hardcode.
- Compose BOM `2024.04.01` (Material 3 1.2.x): `androidx.compose.material3.DatePickerDialog` tersedia, tapi `Devices.DESKTOP` belum ada — preview pakai PHONE/FOLDABLE/TABLET saja.

## Testing
- Hanya ada test placeholder. `ExampleInstrumentedTest` menegaskan package `id.co.brainy` yang SUDAH STALE (namespace asli `id.co.ingatin`) — jangan dijadikan acuan dan jangan langsung dihapus tanpa konfirmasi.
- Proyek TIDAK punya screenshot test (tidak ada source set `screenshotTest`/plugin Compose screenshot). Verifikasi UI manual via `@Preview`.

## Lainnya
- Commit style: `type(scope) : pesan` (spasi sebelum titik dua).
- Skill agent: `.agents/skills/` (adaptive, android-cli, edge-to-edge, r8-analyzer, styles).
- Teks dokumentasi & komentar kode dalam Bahasa Indonesia; copy teks notifikasi mengikuti keputusan user (saat ini English, lihat `ReminderScheduler.kt`). String UI lama masih hardcoded inline (belum pakai `strings.xml`), ikuti pola yang ada.
- README.md STALE di beberapa tempat (`minSdk 24`, klaim WorkManager) — percayai config & kode, bukan README.
