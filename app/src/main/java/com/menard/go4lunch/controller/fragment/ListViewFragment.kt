package com.menard.go4lunch.controller.fragment

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.R
import com.menard.go4lunch.adapter.ListViewAdapter
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.utils.GooglePlacesStreams
import io.reactivex.disposables.CompositeDisposable

class ListViewFragment : BaseFragment() {


    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    /** Progress Bar */
    private lateinit var progressBar: ProgressBar
    /** FusedLocation */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    /** Places Client */
    private lateinit var placesClient: PlacesClient

    companion object {
        fun newInstance(): ListViewFragment {
            return ListViewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.listview_fragment, container, false)

        recyclerView = view.findViewById(R.id.listview_fragment_recycler_view)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))
        progressBar = view.findViewById(R.id.list_view_progress)
        progressBar.indeterminateDrawable.setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //-- Google Places SDK initialization --
        Places.initialize(requireActivity(), BuildConfig.api_key_google)
        placesClient = Places.createClient(requireActivity())


        //-- Layout manager --
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        listOfRestaurant()

        return view
    }


    private fun listOfRestaurant() {
        val locationRequest = setLocationRequest()
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val lastLocation: LatLng = onLocationChanged(locationResult.lastLocation)
                val latitude = lastLocation.latitude.toString()
                val longitude = lastLocation.longitude.toString()

                UserHelper.updateLocation(getCurrentUser().uid, latitude, longitude)
                getResultWithRXJAVA("$latitude,$longitude")
            }
        }, null)
    }



    fun getResultWithRXJAVA(location: String) {
        val disable: CompositeDisposable? = CompositeDisposable()
        disable?.add(GooglePlacesStreams.getDetailsOfSelectedRestaurant(location, "10000", "restaurant", Constants.FIELD_FOR_DETAILS, BuildConfig.api_key_google).subscribe(
                this::handleResponse, this::handleError))
    }

    private fun handleResponse(detailsRequestList: List<DetailsRequest>) {
        progressBar.visibility = View.GONE
        val listViewAdapter = ListViewAdapter(detailsRequestList, requireActivity())
        recyclerView.adapter = listViewAdapter

    }

    private fun handleError(error: Throwable) {
        Log.d(TAG, "Error")
    }


}