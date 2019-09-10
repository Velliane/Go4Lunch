package com.menard.go4lunch.controller.fragment

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.R
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import com.menard.go4lunch.model.nearbysearch.Result
import com.menard.go4lunch.utils.GooglePlacesAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MapviewFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationButtonClickListener {

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
    /** Places Client */
    private lateinit var placesClient: PlacesClient
    private var call: Call<NearbySearch>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.mapview_fragment, container, false)

        mapView = view.findViewById(R.id.mapview)
        mapView.onCreate(savedInstanceState)
        //-- Display the map immediately--
        mapView.onResume()

        //-- Check is fragment is added to MainActivity --
        if (isAdded) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            //-- Google Places SDK initialization --
            Places.initialize(requireActivity(), BuildConfig.api_key_google)
            placesClient = Places.createClient(requireActivity())
        }
        mapView.getMapAsync(this)
        try {
            MapsInitializer.initialize(activity)
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
        mGoogleMap.setOnMyLocationButtonClickListener(this)
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

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val lastLocation: LatLng = onLocationChanged(locationResult.lastLocation)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15F))
                mGoogleMap.addMarker(MarkerOptions().position(lastLocation).title("Coucou").snippet("Click for more information"))

                val retrofit = GooglePlacesAPI.retrofit
                val googlePlacesAPI = retrofit.create(GooglePlacesAPI::class.java)
                call = googlePlacesAPI.getNearbySearch(lastLocation.latitude.toString() + "," + lastLocation.longitude.toString(), "2000", "restaurant", BuildConfig.api_key_google).also {


                    it.enqueue(object : Callback<NearbySearch> {

                        override fun onResponse(call: Call<NearbySearch>, response: Response<NearbySearch>) {
                            if (response.isSuccessful) {
                                val nearbySearch = response.body()
                                val listResults: List<Result> = nearbySearch!!.results

                                for (result in listResults) {

                                    val latLng = LatLng(result.geometry.location.lat, result.geometry.location.lng)
                                    val opening: String = if (result.openingHours != null) {
                                        if (result.openingHours.openNow) {
                                            "Open"
                                        } else {
                                            "Close"
                                        }
                                    } else {
                                        "No opening hours available"
                                    }
                                    mGoogleMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_4373)).title(result.name).snippet(opening))
                                }
                            }
                        }

                        override fun onFailure(call: Call<NearbySearch>, t: Throwable) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    })

                    // Update UI with location data
//                    .addOnSuccessListener { location ->)
//                        if (location != null) {
//                            val lastLocation: LatLng = onLocationChanged(location)
//                            //-- Zoom --
//                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15F))
//                            //-- Add marker --
//                            val geocoder = Geocoder(this.activity!!, Locale.getDefault())
//                            val address: String = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0].getAddressLine(0)
//                            mGoogleMap.addMarker(MarkerOptions().position(lastLocation).title(address).snippet("Click for more information"))
//
//                        } else {
//                            TODO() //Default location and default marker
//                        }


                }
            }
        }, null)
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




