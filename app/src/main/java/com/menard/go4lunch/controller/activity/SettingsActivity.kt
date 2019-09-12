package com.menard.go4lunch.controller.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.menard.go4lunch.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_settings)

        val text: TextView = findViewById(R.id.text_settings)
        val toolbar: androidx.appcompat.widget.Toolbar= findViewById(R.id.toolbar_setting)
        setSupportActionBar(toolbar)

        text.text = "Hello !!!"
    }
}