package com.menard.go4lunch.controller.fragment;

import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.menard.go4lunch.utils.Constants;

public class BaseFragment extends Fragment{



    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //-----------------------------------------//
    //-- CHECK PERMISSIONS FOR FINE LOCATION --//
    //-----------------------------------------//
    /**
     * Check permissions
     */
    public Boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_UPDATE_LOCATION);
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



    //------------------------------//
    //-- GET NEW LOCATION OF USER --//
    //------------------------------//
    /**
     * Get new location
     */
    public LatLng onLocationChanged(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    //--------------------------//
    //-- SET LOCATION REQUEST --//
    //--------------------------//
    public LocationRequest setLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setSmallestDisplacement(50F);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }
}