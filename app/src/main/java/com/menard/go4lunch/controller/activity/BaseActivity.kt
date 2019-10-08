package com.menard.go4lunch.controller.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.menard.go4lunch.R

open class BaseActivity : AppCompatActivity() {


    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun onFailureListener(): OnFailureListener {
        return OnFailureListener { exception ->
            Log.d("Error", exception.printStackTrace().toString())
            val builder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
            builder.setMessage(getString(R.string.error_firestore))
                    .setNegativeButton("Ok"){ dialog, which ->

                    }
                    .create().show()
        }
    }

    fun showSnackBar(linearLayout: LinearLayout, message:String){
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show()
    }

}