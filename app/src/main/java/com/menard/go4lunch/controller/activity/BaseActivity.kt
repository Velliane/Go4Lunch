package com.menard.go4lunch.controller.activity

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

open class BaseActivity : AppCompatActivity() {

    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun onFailureListener(): OnFailureListener {
        return OnFailureListener { exception -> Log.d("Error", exception.printStackTrace().toString())
        }
    }
}