package com.menard.go4lunch.controller.activity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.menard.go4lunch.R;
import com.menard.go4lunch.utils.Constants;

public class BaseActivity extends AppCompatActivity{


    FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    OnFailureListener onFailureListener() {
        return e -> {
            Log.d("Error", e.getLocalizedMessage());
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this, R.style.MyDialogTheme);
            builder.setMessage(getString(R.string.error_firestore))
                    .setNegativeButton("Ok", (dialog, which) -> {
                    })
                    .create().show();
        };
    }

    void showSnackBar(LinearLayout linearLayout, String message){
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    //-- CHECK PERMISSIONS FOR FINE LOCATION --//
    /**
     * Check permissions
     */
    Boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_UPDATE_LOCATION);
            return false;
        }
    }


    /**
     * Result after checking the permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constants.REQUEST_CODE_UPDATE_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Permission granted", "Permission granted");
            }else {
                Log.d("Permission denied", "Permission denied");
            }
        }
    }


    //-- GET NEW LOCATION OF USER --//
    /**
     * Get new location
     */
    LatLng onLocationChanged(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }


    //-- SET LOCATION REQUEST --//
    LocationRequest setLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setSmallestDisplacement(50F);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

}