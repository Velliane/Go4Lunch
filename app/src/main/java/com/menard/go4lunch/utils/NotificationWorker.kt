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
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.controller.activity.MainActivity
import java.util.concurrent.TimeUnit

open class NotificationWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {

    companion object {
        const val NOTIFICATION_ID = 10
        const val NOTIFICATION_TAG = "FIREBASE"


        fun scheduleReminder(data: Data, time: Long) {
            val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setInitialDelay(time, TimeUnit.MINUTES)
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
        val user = inputData.getString(Constants.DATA_USER)
        val restaurantName = inputData.getString(Constants.DATA_RESTAURANT_NAME)
        val restaurantVicinity= inputData.getString(Constants.DATA_RESTAURANT_ADDRESS)
        val listWorker = inputData.getStringArray(Constants.DATA_LIST_WORKMATES)

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

        // Reset Restaurant in Firestore and in SharedPreferences
        UserHelper.updateRestaurant(FirebaseAuth.getInstance().currentUser!!.uid, null, null)
        val sharedPreferences = applicationContext.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.PREF_RESTAURANT_SELECTED, null).apply()

    }
}