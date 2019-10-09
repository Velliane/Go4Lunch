package com.menard.go4lunch.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.menard.go4lunch.R;

public class DispatcherActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher);

        ImageView icon = findViewById(R.id.application_icon);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        icon.startAnimation(animation);

        new Handler().postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                startActivity(new Intent(DispatcherActivity.this, MainActivity.class));
            }else{
                startActivity(new Intent(DispatcherActivity.this, AuthActivity.class));
            }
        }, 3000);
    }

}
