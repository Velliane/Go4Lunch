package com.menard.go4lunch.controller.fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.menard.go4lunch.utils.GooglePlacesStreams
import com.menard.go4lunch.R
import com.menard.go4lunch.controller.activity.LunchActivity
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import com.menard.go4lunch.model.nearbysearch.Result
import com.menard.go4lunch.utils.Constants
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Call

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

                getResultWithRXJAVA(lastLocation.latitude.toString() + "," + lastLocation.longitude.toString())
            }
        }, null)
    }


    fun getResultWithRXJAVA(location: String) {
        val disable: CompositeDisposable? = CompositeDisposable()
        disable?.add(GooglePlacesStreams.getListRestaurant(location, "5000", "restaurant", BuildConfig.api_key_google).subscribe(
                this::handleResponse, this::handleError))

    }

    private fun handleResponse(nearbySearch: NearbySearch) {
        val listResults: List<Result> = nearbySearch.results

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


            mGoogleMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_restaurant)).title(result.name).snippet(opening)).tag = result.placeId
        }
    }

    private fun handleError(error: Throwable) {

        Log.d(TAG, error.localizedMessage)
    }

    //-- ACTION WHEN CLICK ON MARKER --
    override fun onMarkerClick(p0: Marker?): Boolean {
        Toast.makeText(this.context, p0?.title, Toast.LENGTH_LONG).show()
        //-- Return false to centered and open the marker's info window
        return false
    }

    //-- ACTION WHEN CLICK IN INFO WINDOW --
    override fun onInfoWindowClick(p0: Marker?) {
        val intent = Intent(requireActivity(), LunchActivity::class.java)
        intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, p0?.tag.toString())
        startActivity(intent)
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




