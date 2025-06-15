package it.polito.mad.lab5g10.seekscape.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import it.polito.mad.lab5g10.seekscape.MainActivity
import it.polito.mad.lab5g10.seekscape.R
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.NotificationItem

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("FCM_Service", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationTitle = remoteMessage.notification?.title
        val notificationBody = remoteMessage.notification?.body

        Log.d("NOTIFICATION DATA", remoteMessage.data.toString())

        val id = remoteMessage.data["id"]!!
        val type = remoteMessage.data["type"]!!
        val title = remoteMessage.data["title"]!!
        val description = remoteMessage.data["description"]!!
        val tab = remoteMessage.data["tab"]!!
        val navRoute = remoteMessage.data["navRoute"]!!

        sendNotification(notificationTitle, notificationBody, id, type, title, description, tab, navRoute)
    }

    //SEND NOTIFICATION FUNCTION
    private fun sendNotification(
        title: String?,
        messageBody: String?,
        notificationId: String?,
        notificationType: String?,
        notificationTitle: String?,
        notificationDescr: String?,
        notificationTab: String?,
        notificationRoute: String?
    ) {
        val channelId = "fcm_default_channel" //Notification channel identificator
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val contentIntent = Intent(this, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("id", notificationId)
            putExtra("type", notificationType)
            putExtra("title", notificationTitle)
            putExtra("description", notificationDescr)
            putExtra("tab", notificationTab)
            putExtra("navRoute", notificationRoute)
        }

        val contentPendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            contentPendingIntentFlags
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_logo)
            .setContentTitle(title ?: "Notification")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(contentPendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Seekscape Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 , notificationBuilder.build())
    }
}