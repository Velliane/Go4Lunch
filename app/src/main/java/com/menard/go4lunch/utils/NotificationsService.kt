package com.menard.go4lunch.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.menard.go4lunch.controller.activity.MainActivity

class NotificationsService: FirebaseMessagingService() {

    companion object{
        const val NOTIFICATION_ID = 10
        const val NOTIFICATION_TAG = "FIREBASE"

    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification != null){
            val message: String? = remoteMessage.notification!!.body
            Log.e("TAG", message)
            message?.let { sendVisualNotification(it) }
        }
    }

    private fun sendVisualNotification(messageBody: String){

        // Intent shown on click on the notification
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // Notification's style
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle("It's time to eat !")
        inboxStyle.addLine(messageBody)

        // Channel
        val channelId = "id"

        // Notification object
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Go4Lunch")
                .setContentText("Time to eat")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)

        // Add notification to Notification Manager and show it
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "Message provenant de Firebase"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build())
    }
}