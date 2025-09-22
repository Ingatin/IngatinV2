package id.co.brainy.ui.screen.notif

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Task Reminder"
        val desc = inputData.getString("desc") ?: ""
        val message = inputData.getString("message") ?: "Segera selesaikan tugasmu!"
        val context = applicationContext

        ShowSimpleNotification(
            context = context,
            title = title,
            message = message,
            desc = desc
        )

        return Result.success()
    }

}
