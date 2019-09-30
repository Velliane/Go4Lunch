package com.menard.go4lunch.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.menard.go4lunch.controller.activity.LunchActivity
import com.menard.go4lunch.controller.activity.MainActivity

class NotificationService: BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        sendNotification(context)
         }

    fun sendNotification(context: Context){
        val intent = Intent(context, LunchActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,0, intent, 0)

        // Notification's style
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle("It's time to eat !")
        inboxStyle.addLine("Hello Emma, the restaurant you choose is Le Léone at" )

        // Channel
        val channelId = "id"

        // Notification object
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Go4Lunch")
                .setContentText("Time to eat")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)

        // Add notification to Notification Manager and show it
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "Message provenant de Go4Lunch"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Show notification
        notificationManager.notify(NotificationWorker.NOTIFICATION_ID, notificationBuilder.build())

    }
}