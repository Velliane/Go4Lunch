package com.menard.go4lunch.controller.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.R
import kotlinx.android.synthetic.main.activity_dispatcher.*

class DispatcherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispatcher)

        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        application_icon.startAnimation(animation)

        Handler().postDelayed({
            kotlin.run {

                val user = FirebaseAuth.getInstance().currentUser
                if(user != null){
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    startActivity(Intent(this, AuthActivity::class.java))
                }
            }
        }, 3000)

    }
}
