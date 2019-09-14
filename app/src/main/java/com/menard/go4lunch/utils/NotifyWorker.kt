package com.menard.go4lunch.utils

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotifyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {


    fun scheduleReminder(data: Data){
        val notificationWork = PeriodicWorkRequest.Builder(NotifyWorker::class.java, 24, TimeUnit.HOURS, 2, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(data).build()
    }

    fun cancelReminder(){

    }

    override fun doWork(): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}