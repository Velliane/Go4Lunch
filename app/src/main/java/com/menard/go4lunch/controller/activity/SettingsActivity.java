package com.menard.go4lunch.controller.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.menard.go4lunch.R;
import com.menard.go4lunch.api.UserHelper;
import com.menard.go4lunch.model.User;
import com.menard.go4lunch.utils.Constants;

import java.util.*;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    /** Button for Changing displayName */
    private Button buttonChangeName;
    /** Button for deleting account */
    private Button buttonDeleteAccount;
    /** EditText for new displayName */
    private EditText displayNameEdit;
    /** TimePickerDialog */
    private TextView timePicker;
    /** Shared Preferences */
    private SharedPreferences sharedPreferences;
    /** Button Enable Notification */
    private Button enableNotification;
    /** Button Disable Notification */
    private Button disableNotification;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_settings);
        AndroidThreeTen.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar_setting);
        toolbar.setTitle(getString(R.string.title_settings));
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // -- Views --
        buttonChangeName = findViewById(R.id.activity_settings_button);
        buttonChangeName.setOnClickListener(this);
        buttonDeleteAccount = findViewById(R.id.activity_settings_button_delete);
        buttonDeleteAccount.setOnClickListener(this);
        displayNameEdit = findViewById(R.id.activity_settings_display_name);
        timePicker = findViewById(R.id.settings_time_picker);
        timePicker.setOnClickListener(this);
        enableNotification = findViewById(R.id.settings_notifications_yes);
        enableNotification.setOnClickListener(this);
        disableNotification = findViewById(R.id.settings_notifications_no);
        disableNotification.setOnClickListener(this);

        //-- Set button YES/NO clicked according to shared preferences --
        if(sharedPreferences.getBoolean(Constants.PREF_ENABLED_NOTIFICATIONS, true)){
            setEnableNotification();
        }else{
            setDisableNotification();
        }
        //-- Set time --
        int hour = sharedPreferences.getInt(Constants.PREF_NOTIFICATIONS_HOURS, 12);
        int minute = sharedPreferences.getInt(Constants.PREF_NOTIFICATIONS_MINUTES, 0);
        String minutes;
        if(minute == 0){
            minutes = "00";
        }else {
            minutes = String.valueOf(minute);
        }
        timePicker.setText(getString(R.string.time_picker, hour, minutes));


        //-- Find display name on Firestore --
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User currentUser = documentSnapshot.toObject(User.class);
            assert currentUser != null;
            String displayName = currentUser.getUserName();
            displayNameEdit.setText(displayName);
        });

    }

    @Override
    public void onClick(View v) {

        if (v == buttonChangeName) {
            UserHelper.updateName(displayNameEdit.getText().toString(), getCurrentUser().getUid()).addOnFailureListener(onFailureListener());
            Toast.makeText(this, R.string.settings_change_name_validation, Toast.LENGTH_SHORT).show();
        }
        if (v == buttonDeleteAccount) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle(R.string.alert_dialog_settings_title)
                    .setMessage(R.string.alert_dialog_settings_text)
                    .setPositiveButton(R.string.alert_dialog_settings_ok, (dialog, which) ->
                            deleteAccount()
                    )
                    .setNegativeButton(R.string.alert_dialog_settings_no, (dialog, which) ->
                            Toast.makeText(this, R.string.alert_dialog_settings_good, Toast.LENGTH_SHORT).show()
                    )
                    .create().show();
        }
        if (v == timePicker) {
            showTimePicker();
        }
        if(v == enableNotification){
            if(!sharedPreferences.getBoolean(Constants.PREF_ENABLED_NOTIFICATIONS, true)){
                sharedPreferences.edit().putBoolean(Constants.PREF_ENABLED_NOTIFICATIONS, true).apply();
                setEnableNotification();
            }
        }
        if(v == disableNotification){
            if(sharedPreferences.getBoolean(Constants.PREF_ENABLED_NOTIFICATIONS, true)){
                sharedPreferences.edit().putBoolean(Constants.PREF_ENABLED_NOTIFICATIONS, false).apply();
                setDisableNotification();
            }
        }
    }


    /**
     * Show a TimePickerDialog to the user
     */
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        TimePickerDialog picker = new TimePickerDialog(this, R.style.MyDialogTheme, (view, hourOfDay, minute) -> {
            timePicker.setText(getString(R.string.time_picker, hourOfDay, String.valueOf(minute)));
            sharedPreferences.edit().putInt(Constants.PREF_NOTIFICATIONS_HOURS, hourOfDay).apply();
            sharedPreferences.edit().putInt(Constants.PREF_NOTIFICATIONS_MINUTES, minute).apply();
        }, hour, minutes, false);
        picker.show();


    }

    /**
     * Delete account from Firebase
     */
    private void deleteAccount() {
        //-- Delete from Firestore --
        UserHelper.deleteUser(getCurrentUser().getUid()).addOnFailureListener(onFailureListener());
        //-- Delete from Firebase Auth --
        getCurrentUser().delete();
        //-- Start AuthActivity --
        startActivity(new Intent(this, AuthActivity.class));
    }

    private void setEnableNotification(){
        enableNotification.setBackgroundColor(this.getResources().getColor(R.color.current_user_message_background));
        disableNotification.setBackgroundColor(this.getResources().getColor(R.color.navigation_drawer_background));
    }

    private void setDisableNotification(){
        enableNotification.setBackgroundColor(this.getResources().getColor(R.color.navigation_drawer_background));
        disableNotification.setBackgroundColor(this.getResources().getColor(R.color.current_user_message_background));
    }

}