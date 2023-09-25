package ua.syt0r.kanji.core.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ua.syt0r.kanji.presentation.common.resources.string.getStrings

class ReminderNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat
) : ReminderNotificationContract.Manager {

    companion object {
        private const val ReminderNotificationChannelId = "reminder_notification_channel"
        private const val ReminderNotificationId = 1
    }

    @SuppressLint("MissingPermission")
    override fun showNotification(learn: Int, review: Int) {
        val strings = getStrings().reminderNotification

        val channel = NotificationChannelCompat
            .Builder(ReminderNotificationChannelId, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(strings.channelName)
            .build()

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, ReminderNotificationChannelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(strings.title)
            .setContentText(strings.message(learn, review))
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    context.packageManager.getLaunchIntentForPackage(context.packageName),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(ReminderNotificationId, notification)
    }

    override fun dismissNotification() {
        notificationManager.cancel(ReminderNotificationId)
    }

}