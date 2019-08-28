package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.menard.go4lunch.R
import java.lang.Exception

class MapviewFragment: Fragment(), OnMapReadyCallback{


    companion object {

        fun newInstance(): MapviewFragment {
            return MapviewFragment()
        }
    }

    private lateinit var mapView: MapView
    private lateinit var mGoogleMap: GoogleMap


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.mapview_fragment, container, false)

        mapView = view.findViewById(R.id.mapview)
        mapView.onCreate(savedInstanceState)
        //-- Display the map immediately--
        mapView.onResume()
        mapView.getMapAsync (this)

        try {
            MapsInitializer.initialize(this.activity)
        }catch (e: Exception){
            e.printStackTrace()
        }


        return view
    }



    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap!!
        mGoogleMap.uiSettings.isZoomControlsEnabled
        mGoogleMap.isMyLocationEnabled
        val sydney = LatLng((-34).toDouble(), 151.0)
        mGoogleMap.addMarker(MarkerOptions().position(sydney).title("Title").snippet("Description"))

        val cameraPosition = CameraPosition.builder().target(sydney).zoom(12.0F).build()
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


}


