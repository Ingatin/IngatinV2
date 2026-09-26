package id.co.ingatin.platform.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

/** Action broadcast untuk alarm pengingat task. */
const val ACTION_TASK_REMINDER = "id.co.ingatin.ACTION_TASK_REMINDER"

/** Extra payload alarm pengingat. */
const val EXTRA_TITLE = "extra_title"
const val EXTRA_DESC = "extra_desc"
const val EXTRA_MESSAGE = "extra_message"
const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
const val EXTRA_IS_DUE_NOW = "extra_is_due_now"

/**
 * API penjadwalan pengingat yang dilihat UI layer.
 * Implementasi memakai AlarmManager + BroadcastReceiver.
 */
interface ReminderScheduler {

    /**
     * Jadwalkan pengingat untuk sebuah task — satu alarm per offset
     * (10 menit sebelum deadline & tepat pada [dueMillis]).
     */
    fun schedule(taskId: String, title: String, description: String, dueMillis: Long)

    /** Batalkan semua alarm milik sebuah task (dipanggil saat task dihapus/diubah). */
    fun cancel(taskId: String)

    /**
     * true jika aplikasi boleh menjadwalkan exact alarm di perangkat ini.
     * Wajib agar pengingat tetap tepat waktu di Android 14+ dengan
     * targetSdk 34+.
     */
    fun canScheduleExact(): Boolean

    /** Arahkan user ke pengaturan "Alarms & reminders" (Android 12+). */
    fun openExactAlarmSettings()
}

class AlarmReminderSchedulerImpl(
    private val context: Context,
    private val ledger: ReminderLedger
) : ReminderScheduler {

    override fun schedule(
        taskId: String,
        title: String,
        description: String,
        dueMillis: Long
    ) {
        val now = System.currentTimeMillis()
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        cancelLegacyAlarms(taskId)
        val exactAllowed = canScheduleExact()
        var scheduledAny = false

        for (offset in REMINDER_OFFSETS_MINUTES) {
            val triggerAt = dueMillis - offset * 60_000L
            if (triggerAt <= now) continue
            val isDueNow = offset == 0L
            val message = if (isDueNow)
                "It's time! The task \"$title\" is due now."
            else
                "The task \"$title\" is due in $offset minutes!"
            val pendingIntent =
                reminderPendingIntent(taskId, title, description, message, offset)
            try {
                if (exactAllowed) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent
                    )
                } else {
                    // Fallback inexact bila izin exact alarm belum diberikan.
                    alarmManager.setWindow(
                        AlarmManager.RTC_WAKEUP,
                        maxOf(triggerAt - 30_000L, now),
                        60_000L,
                        pendingIntent
                    )
                }
                scheduledAny = true
            } catch (e: SecurityException) {
                Log.e(TAG, "schedule failure", e)
            }
        }

        if (scheduledAny) ledger.save(taskId, title, description, dueMillis)
        else ledger.clear(taskId)
    }

    override fun cancel(taskId: String) {
        cancelLegacyAlarms(taskId)
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        for (offset in REMINDER_OFFSETS_MINUTES) {
            val pendingIntent = reminderPendingIntent(taskId, "", "", "", offset)
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        ledger.clear(taskId)
    }

    override fun canScheduleExact(): Boolean =
        ExactAlarmPermission.canScheduleExact(context)

    override fun openExactAlarmSettings() =
        ExactAlarmPermission.openExactAlarmSettings(context)

    /** Batalkan alarm dari offset lama yang tak lagi dipakai (lihat [LEGACY_REMINDER_OFFSETS_MINUTES]). */
    private fun cancelLegacyAlarms(taskId: String) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        for (offset in LEGACY_REMINDER_OFFSETS_MINUTES) {
            if (offset in REMINDER_OFFSETS_MINUTES) continue
            val pendingIntent = reminderPendingIntent(taskId, "", "", "", offset)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Kode permintaan PendingIntent sekaligus notification ID.
     * Selalu non-negatif: String.hashCode() bisa negatif padahal dipakai
     * ganda di kedua tempat tersebut.
     */
    private fun requestCodeFor(taskId: String, offsetMinutes: Long): Int =
        "${taskId}_$offsetMinutes".hashCode() and 0x7FFFFFFF

    private fun reminderPendingIntent(
        taskId: String,
        title: String,
        description: String,
        message: String,
        offsetMinutes: Long
    ): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_DESC, description)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_NOTIFICATION_ID, requestCodeFor(taskId, offsetMinutes))
            putExtra(EXTRA_IS_DUE_NOW, offsetMinutes == 0L)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCodeFor(taskId, offsetMinutes),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        /** Offset pengingat, dalam menit. 0 berarti tepat pada deadline. */
        private val REMINDER_OFFSETS_MINUTES = listOf(10L, 0L)

        /**
         * Offset lama dari versi sebelum perubahan — hanya dipakai untuk
         * membatalkan alarm usang agar tidak ada notifikasi ganda setelah
         * update aplikasi.
         */
        private val LEGACY_REMINDER_OFFSETS_MINUTES = listOf(1L)

        private const val TAG = "AlarmReminderScheduler"
    }
}
