package com.menard.go4lunch.controller.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.User

class SettingsActivity : BaseActivity(), View.OnClickListener {


    lateinit var buttonChangeName: Button
    lateinit var buttonDeleteAccount: Button
    lateinit var displayNameEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_settings)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_setting)
        toolbar.title = "Settings"
        setSupportActionBar(toolbar)

//        val query: Query = UserHelper.getUserName(getCurrentUser().uid)

        buttonChangeName = findViewById(R.id.activity_settings_button)
        buttonChangeName.setOnClickListener(this)
        buttonDeleteAccount = findViewById(R.id.activity_settings_button_delete)
        buttonDeleteAccount.setOnClickListener(this)
        displayNameEdit = findViewById(R.id.activity_settings_display_name)
        displayNameEdit.setText(getCurrentUser().displayName)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.activity_settings_button -> {
                UserHelper.updateName(displayNameEdit.text.toString(), getCurrentUser().uid)
                Toast.makeText(this, "Your display name have been updated", Toast.LENGTH_SHORT).show()
            }

            R.id.activity_settings_button_delete -> {
                val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                builder.setTitle("Are you sure ?")
                        .setMessage("Do your really want to delete your account ? This action will be irreversible")
                        .setPositiveButton("Yes, I'm sure") { dialog, which ->
                            deleteAccount()
                        }
                        .setNegativeButton("No, I change my mind") { dialog, which ->
                            Toast.makeText(this, "Good decision !", Toast.LENGTH_SHORT).show()
                        }
                        .create().show()
            }
        }
    }

    fun deleteAccount() {
        //-- Delete from Firestore --
        UserHelper.deleteUser(getCurrentUser().uid)
        //-- Delete from Firebase Auth --
        getCurrentUser().delete()
        //-- Start AuthActivity --
        startActivity(Intent(this, AuthActivity::class.java))
    }
}