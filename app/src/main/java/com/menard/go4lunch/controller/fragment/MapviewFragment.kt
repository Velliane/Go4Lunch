package com.menard.go4lunch.controller.fragment

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.menard.go4lunch.Constants.Companion.REQUEST_CODE_UPDATE_LOCATION
import com.menard.go4lunch.R
import java.util.*

class MapviewFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationButtonClickListener {

    companion object {

        fun newInstance(): MapviewFragment {
            return MapviewFragment()
        }
    }

    /** Map View */
    private lateinit var mapView: MapView
    /** Google Map */
    private lateinit var mGoogleMap: GoogleMap
    /** FusedLocation */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.mapview_fragment, container, false)

        mapView = view.findViewById(R.id.mapview)
        mapView.onCreate(savedInstanceState)
        //-- Display the map immediately--
        mapView.onResume()

        //-- Check is fragment is added to MainActivity --
        if (isAdded) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        }

        mapView.getMapAsync(this)
        try {
            MapsInitializer.initialize(this.activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap!!
        mGoogleMap.uiSettings.isZoomControlsEnabled
        mGoogleMap.isMyLocationEnabled
        //-- Check Permissions for ACCESS_FINE_LOCATION
        if (checkPermissions()) {
            mGoogleMap.isMyLocationEnabled = true
            GPSUpdateLocation()
        }
        mGoogleMap.setOnMarkerClickListener(this)
        mGoogleMap.setOnInfoWindowClickListener(this)
        mGoogleMap.setOnMyLocationButtonClickListener (this)
    }

    //-- UPDATE MAP WITH USER'S LOCATION --
    /**
     * Update Map with new location of the user
     */
    fun GPSUpdateLocation() {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 2000
        locationRequest.smallestDisplacement = 50F
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                val lastLocation: LatLng = onLocationChanged(location)

                //-- Zoom --
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15F))
                //-- Add marker --
                val geocoder = Geocoder(this.activity!!, Locale.getDefault())
                val address: String = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0].getAddressLine(0)
                mGoogleMap.addMarker(MarkerOptions().position(lastLocation).title(address).snippet("Click for more information"))

            } else {
                TODO() //Default location and default marker
            }
        }
    }

    /**
     * Get new location
     */
    fun onLocationChanged(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    //-- PERMISSIONS FOR ACCESS_FINE_LOCATION --
    /**
     * Check permissions
     */
    fun checkPermissions(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this.activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_UPDATE_LOCATION)
            false
        }
    }

    /**
     * Result after checking the permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_UPDATE_LOCATION -> {
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

    //-- ACTION WHEN CLICK ON MARKER --
    override fun onMarkerClick(p0: Marker?): Boolean {
        Toast.makeText(this.context, p0?.title, Toast.LENGTH_LONG).show()
        //-- Return false to centered and open the marker's info window
        return false
    }

    //-- ACTION WHEN CLICK IN INFO WINDOW --
    override fun onInfoWindowClick(p0: Marker?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //-- ACTION WHEN CLICK ON MY LOCATION BUTTON
    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    //-- FRAGMENT LIFE --

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}


