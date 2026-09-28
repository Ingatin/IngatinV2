package id.co.ingatin.platform.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import id.co.ingatin.R

private const val CHANNEL_REMINDER_ID = "channel_id"
private const val CHANNEL_DUE_ID = "channel_id_due"

/** Membangun & menampilkan notifikasi pengingat task. */
object Notifier {

    fun show(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 1001,
        isDueNow: Boolean = false
    ) {
        val channelId = if (isDueNow) CHANNEL_DUE_ID else CHANNEL_REMINDER_ID
        val channelName = if (isDueNow) "Due Now" else "My Channel"
        val importance = if (isDueNow) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            NotificationManager.IMPORTANCE_DEFAULT
        }

        val channel = NotificationChannel(
            channelId,
            channelName,
            importance
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        // Bangun notifikasi
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.iconingatin)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(
                if (isDueNow) NotificationCompat.PRIORITY_MAX
                else NotificationCompat.PRIORITY_HIGH
            )
            .setAutoCancel(true)
        if (isDueNow) {
            builder.setCategory(NotificationCompat.CATEGORY_ALARM)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        }


        // Tampilkan notifikasi
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }
    }
}
