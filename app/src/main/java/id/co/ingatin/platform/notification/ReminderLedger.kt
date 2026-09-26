package id.co.ingatin.platform.notification

import android.content.Context
import android.content.SharedPreferences

/**
 * Catatan alarm pengingat di SharedPreferences (ledger) agar bisa
 * dijadwalkan ulang setelah reboot via [BootReceiver].
 */
class ReminderLedger(context: Context) {

    private val storage: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Jadwalkan ulang semua alarm yang masih di masa depan (dipanggil [BootReceiver]). */
    fun rescheduleAll(scheduler: ReminderScheduler) {
        val now = System.currentTimeMillis()
        val taskIds = storage.getStringSet(KEY_TASK_IDS, emptySet()).orEmpty().toList()
        for (taskId in taskIds) {
            val due = storage.getLong(keyDue(taskId), 0L)
            if (due <= now) {
                clear(taskId)
                continue
            }
            val title = storage.getString(keyTitle(taskId), null) ?: continue
            val desc = storage.getString(keyDesc(taskId), "") ?: ""
            scheduler.schedule(taskId, title, desc, due)
        }
    }

    fun save(taskId: String, title: String, description: String, dueMillis: Long) {
        val taskIds = storage.getStringSet(KEY_TASK_IDS, emptySet()).orEmpty().toMutableSet()
        taskIds.add(taskId)
        storage.edit()
            .putStringSet(KEY_TASK_IDS, taskIds)
            .putLong(keyDue(taskId), dueMillis)
            .putString(keyTitle(taskId), title)
            .putString(keyDesc(taskId), description)
            .apply()
    }

    fun clear(taskId: String) {
        val taskIds = storage.getStringSet(KEY_TASK_IDS, emptySet()).orEmpty().toMutableSet()
        taskIds.remove(taskId)
        storage.edit()
            .putStringSet(KEY_TASK_IDS, taskIds)
            .remove(keyDue(taskId))
            .remove(keyTitle(taskId))
            .remove(keyDesc(taskId))
            .apply()
    }

    private fun keyDue(taskId: String) = "t_${taskId}_due"
    private fun keyTitle(taskId: String) = "t_${taskId}_title"
    private fun keyDesc(taskId: String) = "t_${taskId}_desc"

    companion object {
        private const val PREFS_NAME = "reminder_alarms"
        private const val KEY_TASK_IDS = "task_ids"
    }
}
