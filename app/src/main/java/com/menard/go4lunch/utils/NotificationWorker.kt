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
import com.menard.go4lunch.model.User
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

open class NotificationWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {

    companion object {
        const val NOTIFICATION_ID = 10
        private const val TAG_NOTIFICATION = "EATING_TIME"


        @JvmStatic
        fun scheduleReminder(data: Data, time: Long) {
            val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .setInitialDelay(time, TimeUnit.MINUTES)
                    //.setInitialDelay(45, TimeUnit.SECONDS)
                    .addTag(TAG_NOTIFICATION)
                    .setInputData(data).build()

            val instance = WorkManager.getInstance()
            instance.enqueueUniqueWork(TAG_NOTIFICATION, ExistingWorkPolicy.REPLACE, notificationWork)
        }

        @JvmStatic
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
        val restaurantVicinity = inputData.getString(Constants.DATA_RESTAURANT_ADDRESS)

        sendNotification(user, restaurantId!!, restaurantName, restaurantVicinity)

        return Result.success()
    }



    private fun sendNotification(user: String?, restaurantId: String, restaurantName: String?, restaurantVicinity: String?) {
        val intent = Intent(applicationContext, LunchActivity::class.java)
        intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, restaurantId)

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Notification object
        val stringBuilder = StringBuilder()
        UserHelper.getUsersCollection().get().addOnSuccessListener { result ->
            for (userId in result) {
                val users = userId.toObject(User::class.java)
                if(users.userId != FirebaseAuth.getInstance().currentUser!!.uid) {
                    if (users.userRestaurantId == restaurantId) {
                        stringBuilder.append(users.userName + ",")
                    }
                }
            }

            val text = if (stringBuilder.toString() == "") {
                applicationContext.getString(R.string.notification_alone, user, restaurantName, restaurantVicinity)
            } else {
                applicationContext.getString(R.string.notification_with_workmates, user, restaurantName, stringBuilder.toString(), restaurantVicinity)
            }

            val channelId = "id"
            val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
                    .setContentTitle(applicationContext.getString(R.string.notification_title))
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(text))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelName = applicationContext.getString(R.string.channel)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(channel)
            }

            // Show notification
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }

        // Reset Restaurant in Firestore and in SharedPreferences
        UserHelper.updateRestaurant(FirebaseAuth.getInstance().currentUser!!.uid, null, null)
        val sharedPreferences = applicationContext.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.PREF_RESTAURANT_SELECTED, null).apply()

    }
}