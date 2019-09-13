package com.menard.go4lunch.utils

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {


    fun scheduleReminder(data: Data){

    }

    fun cancelReminder(){

    }

    override fun doWork(): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}