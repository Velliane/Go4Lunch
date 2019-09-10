package com.menard.go4lunch.controller.fragment

import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import com.menard.go4lunch.model.nearbysearch.Result
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.utils.GooglePlacesAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

open class BaseFragment : Fragment(){




    //-- CHECK PERMISSIONS FOR FINE LOCATION --
    /**
     * Check permissions
     */
    open fun checkPermissions(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this.activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), Constants.REQUEST_CODE_UPDATE_LOCATION)
            false
        }
    }

    /**
     * Result after checking the permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.REQUEST_CODE_UPDATE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //GPSUpdateLocation()
                    Log.d("Permission granted", "Permission granted")
                } else {
                    Log.d("Permission denied", "Permission denied")
                }
                return
            }
        }
    }

    //-- GET NEW LOCATION OF USER --
    /**
     * Get new location
     */
    open fun onLocationChanged(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

}