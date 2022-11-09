package com.likemagic.masters_beside.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.view.MainActivity


class PushService:FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.isLooping = false
        }

        val builder = NotificationCompat.Builder(this, "CHANNEL_ID")
        builder.setSmallIcon(R.drawable.icon_main)

        val resultIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(this, 1, resultIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(this, 1, resultIntent, FLAG_UPDATE_CURRENT)
            }

        builder.setContentTitle(message.notification?.title)
        builder.setContentText(message.notification?.body)
        builder.setContentIntent(pendingIntent)
        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(message.notification?.body)
        )
        builder.setAutoCancel(true)
        builder.priority = Notification.PRIORITY_MAX

         val mNotificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "MESSAGES"
            val channel = NotificationChannel(
                channelId,
                "Сообщения",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }

        mNotificationManager.notify(100, builder.build())
    }

}