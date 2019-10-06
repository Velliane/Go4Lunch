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
import com.menard.go4lunch.R
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.controller.activity.LunchActivity
import java.util.concurrent.TimeUnit

open class NotificationWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {

    companion object {
        const val NOTIFICATION_ID = 10


        fun scheduleReminder(data: Data, time: Long) {
            val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setInitialDelay(time, TimeUnit.MINUTES)
                    .addTag("EATING_TIME")
                    .setInputData(data).build()

            val instance = WorkManager.getInstance()
            instance.enqueueUniqueWork("EATING_TIME", ExistingWorkPolicy.REPLACE,notificationWork)
        }

         fun cancelReminder() {
            val instance = WorkManager.getInstance()
            instance.cancelAllWork()
        }
    }



    override fun doWork(): Result {

        //-- Get Data --
        val user = inputData.getString(Constants.DATA_USER)
        val restaurantId = inputData.getString(Constants.DATA_RESTAURANT_ID)
        val restaurantName = inputData.getString(Constants.DATA_RESTAURANT_NAME)
        val restaurantVicinity= inputData.getString(Constants.DATA_RESTAURANT_ADDRESS)
        val listWorker = inputData.getString(Constants.DATA_LIST_WORKMATES)

        sendNotification(user,restaurantId!!, restaurantName, restaurantVicinity, listWorker)

        return Result.success()
     }

    private fun sendNotification(user: String?, restaurantId: String,  restaurantName: String?, restaurantVicinity: String?, list: String?){
        val intent = Intent(applicationContext, LunchActivity::class.java)
        intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER,restaurantId)

        val pendingIntent = PendingIntent.getActivity(applicationContext,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Channel
        val channelId = "id"
        // Notification object
        val text = if(list == null){
            "Hello $user, the restaurant you choose is $restaurantName at $restaurantVicinity."
        }else{
            "Hello $user, the restaurant you choose is $restaurantName with $list at $restaurantVicinity."
        }
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle("Time to eat !")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "Message provenant de Go4Lunch"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        // Reset Restaurant in Firestore and in SharedPreferences
        UserHelper.updateRestaurant(FirebaseAuth.getInstance().currentUser!!.uid, null, null)
        val sharedPreferences = applicationContext.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.PREF_RESTAURANT_SELECTED, null).apply()

    }
}