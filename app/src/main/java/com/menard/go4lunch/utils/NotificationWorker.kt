package com.menard.go4lunch.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.menard.go4lunch.controller.activity.MainActivity
import org.threeten.bp.LocalTime
import java.time.Duration
import java.util.concurrent.TimeUnit

open class NotificationWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {

    companion object {
        const val NOTIFICATION_ID = 10
        const val NOTIFICATION_TAG = "FIREBASE"


        fun scheduleReminder(data: Data, time: Long) {
            val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setInitialDelay(time, TimeUnit.MINUTES)
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .setInputData(data).build()

            val instance = WorkManager.getInstance()
            instance.enqueue(notificationWork)
        }

         fun cancelReminder() {
            val instance = WorkManager.getInstance()
            instance.cancelAllWork()
        }
    }



    override fun doWork(): Result {

        //-- Get Date --
        val user = inputData.getString("UserName")
        val restaurantName = inputData.getString("restaurantName")
        val restaurantVicinity= inputData.getString("vicinity")
        val listWorker = inputData.getStringArray("list")

        sendNotification(user, restaurantName, restaurantVicinity, listWorker)

        return Result.success()
     }

    private fun sendNotification(user: String?, restaurantName: String?, restaurantVicinity: String?, list: Array<String>?){
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext,0, intent, 0)

        // Notification's style
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle("It's time to eat !")
        inboxStyle.addLine("Hello $user, the restaurant you choose is $restaurantName at $restaurantVicinity")

        // Channel
        val channelId = "id"

        // Notification object
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle("Go4Lunch")
                .setContentText("Time to eat")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)

        // Add notification to Notification Manager and show it
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "Message provenant de Go4Lunch"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build())

    }
}