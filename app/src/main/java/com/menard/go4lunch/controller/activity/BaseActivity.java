package com.menard.go4lunch.controller.activity;

import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.menard.go4lunch.R;

public class BaseActivity extends AppCompatActivity{


    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public OnFailureListener onFailureListener() {
        return e -> {
            Log.d("Error", e.getLocalizedMessage());
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this, R.style.MyDialogTheme);
            builder.setMessage(getString(R.string.error_firestore))
                    .setNegativeButton("Ok", (dialog, which) -> {
                    })
                    .create().show();
        };
    }

    public void showSnackBar(LinearLayout linearLayout,String message){
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}