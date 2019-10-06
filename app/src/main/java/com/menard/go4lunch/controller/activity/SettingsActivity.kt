package com.menard.go4lunch.controller.activity

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.jakewharton.threetenabp.AndroidThreeTen
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.User
import java.util.*

class SettingsActivity : BaseActivity(), View.OnClickListener {

    /** Button for Changing displayName */
    private lateinit var buttonChangeName: Button
    /** Button for deleting account */
    private lateinit var buttonDeleteAccount: Button
    /** EditText for new displayName */
    private lateinit var displayNameEdit: EditText
    /** TimePickerDialog */
    private lateinit var timePicker: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_settings)
        AndroidThreeTen.init(this)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_setting)
        toolbar.title = getString(R.string.title_settings)
        setSupportActionBar(toolbar)

        buttonChangeName = findViewById(R.id.activity_settings_button)
        buttonChangeName.setOnClickListener(this)
        buttonDeleteAccount = findViewById(R.id.activity_settings_button_delete)
        buttonDeleteAccount.setOnClickListener(this)
        displayNameEdit = findViewById(R.id.activity_settings_display_name)
        timePicker = findViewById(R.id.settings_time_picker)
        timePicker.setOnClickListener(this)

        //-- Find display name on Firestore --
        UserHelper.getUser(getCurrentUser().uid).addOnSuccessListener { documentSnapshot ->
            val currentUser = documentSnapshot.toObject<User>(User::class.java)
            val displayName = currentUser!!.userName
            displayNameEdit.setText(displayName)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.activity_settings_button -> {
                UserHelper.updateName(displayNameEdit.text.toString(), getCurrentUser().uid)
                Toast.makeText(this, R.string.settings_change_name_validation, Toast.LENGTH_SHORT).show()
            }

            R.id.activity_settings_button_delete -> {
                val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                builder.setTitle(R.string.alert_dialog_settings_title)
                        .setMessage(R.string.alert_dialog_settings_text)
                        .setPositiveButton(R.string.alert_dialog_settings_ok) { dialog, which ->
                            deleteAccount()
                        }
                        .setNegativeButton(R.string.alert_dialog_settings_no) { dialog, which ->
                            Toast.makeText(this, R.string.alert_dialog_settings_good , Toast.LENGTH_SHORT).show()
                        }
                        .create().show()
            }

            R.id.settings_time_picker -> { showTimePicker()}
        }
    }

    /**
     * Show a TimePickerDialog to the user
     */
    private fun showTimePicker(){
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
       val timePicker = TimePickerDialog(this, R.style.MyDialogTheme, TimePickerDialog.OnTimeSetListener( function = { view, hourOfDay, minute ->
           timePicker.text = "$hourOfDay:$minute"
        }), hour, minutes, false)
        timePicker.show()

    }

    /**
     * Delete account from Firebase
     */
    private fun deleteAccount() {
        //-- Delete from Firestore --
        UserHelper.deleteUser(getCurrentUser().uid)
        //-- Delete from Firebase Auth --
        getCurrentUser().delete()
        //-- Start AuthActivity --
        startActivity(Intent(this, AuthActivity::class.java))
    }
}