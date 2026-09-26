package id.co.ingatin.platform.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat

/** Helper izin exact alarm — satu-satunya bagian platform yang dipanggil dari UI. */
object ExactAlarmPermission {

    private const val TAG = "ExactAlarmPermission"

    /**
     * true jika aplikasi boleh menjadwalkan exact alarm di perangkat ini.
     * USE_EXACT_ALARM (API 33+, auto-granted saat install & tak bisa dicabut
     * user) didahulukan; jika tidak dipegang, fallback ke izin
     * SCHEDULE_EXACT_ALARM yang bisa dicabut user (Android 12+).
     */
    @SuppressLint("InlinedApi")
    fun canScheduleExact(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED
        ) return true
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return false
        return alarmManager.canScheduleExactAlarms()
    }

    /** Arahkan user ke pengaturan "Alarms & reminders" (Android 12+). */
    @SuppressLint("InlinedApi")
    fun openExactAlarmSettings(context: Context) {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "openExactAlarmSettings failure", e)
        }
    }
}
