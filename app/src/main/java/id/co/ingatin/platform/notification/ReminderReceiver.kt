package id.co.ingatin.platform.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Menerima alarm dari [ReminderScheduler] lalu menampilkan notifikasi.
 * Berjalan walau aplikasi sedang tidak dibuka.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TASK_REMINDER) return

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Pengingat Tugas"
        val desc = intent.getStringExtra(EXTRA_DESC) ?: ""
        val message = intent.getStringExtra(EXTRA_MESSAGE)
            ?: "Segera selesaikan tugasmu!"
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)
        val isDueNow = intent.getBooleanExtra(EXTRA_IS_DUE_NOW, false)

        Notifier.show(
            context = context.applicationContext,
            title = title,
            desc = desc,
            message = message,
            notificationId = notificationId,
            isDueNow = isDueNow
        )
    }
}
