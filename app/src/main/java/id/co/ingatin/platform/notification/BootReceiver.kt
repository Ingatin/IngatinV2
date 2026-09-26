package id.co.ingatin.platform.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Menjadwalkan ulang semua alarm pengingat setelah perangkat reboot
 * atau aplikasi diperbarui, karena AlarmManager tidak bertahan
 * melewati reboot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in BOOT_ACTIONS) return
        val pending = goAsync()
        try {
            val appContext = context.applicationContext
            val ledger = ReminderLedger(appContext)
            val scheduler = AlarmReminderSchedulerImpl(appContext, ledger)
            ledger.rescheduleAll(scheduler)
        } finally {
            pending.finish()
        }
    }

    companion object {
        private val BOOT_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON"
        )
    }
}
