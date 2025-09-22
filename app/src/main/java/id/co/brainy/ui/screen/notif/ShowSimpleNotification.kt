package id.co.brainy.ui.screen.notif

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import id.co.brainy.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


fun ShowSimpleNotification(context: Context, title: String, desc: String, message: String) {
    val channelId = "channel_id"
    val channelName = "My Channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    // Bangun notifikasi
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.iconbrainy)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(desc))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)


    // Tampilkan notifikasi
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        NotificationManagerCompat.from(context).notify(1001, builder.build())
    }
}

fun getTimeRemainingText(targetDateTime: String): String {
    val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

    return try {
        val now = Calendar.getInstance().time
        val target = format.parse(targetDateTime)

        if (target != null) {
            val diffInMillis = target.time - now.time

            if (diffInMillis <= 0) {
                "the time is over"
            } else {
                val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60

                when {
                    days > 0 -> "$days day left"
                    hours > 0 -> "$hours hours left"
                    minutes > 0 -> "$minutes minutes left"
                    else -> "is time"
                }
            }
        } else {
            "Incorrect date format"
        }
    } catch (e: Exception) {
        "Failed to calculate time"
    }
}

fun scheduleReminder(context: Context, taskTitle: String, taskDesc: String, dateTime: String) {
    val format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    val targetTime = format.parse(dateTime) ?: return
    val now = Calendar.getInstance().timeInMillis
    val targetMillis = targetTime.time

    // 1. Notifikasi 10 menit sebelum
    val tenMinBeforeMillis = targetMillis - TimeUnit.MINUTES.toMillis(10)
    val delayTenMin = tenMinBeforeMillis - now
    if (delayTenMin > 0) {
        val data10Min = workDataOf(
            "title" to taskTitle,
            "desc" to taskDesc,
            "message" to "Task \"$taskTitle\" will be due in 10 minutes!"
        )

        val request10Min = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data10Min)
            .setInitialDelay(delayTenMin, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request10Min)
    }

    // 2. Notifikasi 5 detik sebelum waktu target
    val fiveSecBeforeMillis = targetMillis - TimeUnit.MINUTES.toMillis(1)
    val delayFiveSec = fiveSecBeforeMillis - now
    if (delayFiveSec > 0) {
        val data5Sec = workDataOf(
            "title" to taskTitle,
            "desc" to taskDesc,
            "message" to "Task \"$taskTitle\" is almost due in 1 minutes!"
        )

        val request5Sec = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data5Sec)
            .setInitialDelay(delayFiveSec, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request5Sec)
    }
}


