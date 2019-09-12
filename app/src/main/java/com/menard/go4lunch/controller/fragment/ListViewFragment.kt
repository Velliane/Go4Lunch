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
import com.menard.go4lunch.utils.GooglePlacesStreams
import com.menard.go4lunch.R
import com.menard.go4lunch.adapter.ListViewAdapter
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import com.menard.go4lunch.model.nearbysearch.Result
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Call

class ListViewFragment : BaseFragment() {


    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    /** FusedLocation */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    /** Places Client */
    private lateinit var placesClient: PlacesClient
    private var call: Call<NearbySearch>? = null

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

        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 2000
        locationRequest.smallestDisplacement = 50F
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val lastLocation: LatLng = onLocationChanged(locationResult.lastLocation)
                getResultWithRXJAVA(lastLocation.latitude.toString() + "," + lastLocation.longitude.toString())

            }
        }, null)
    }


    fun getResultWithRXJAVA(location: String) {
        val disable: CompositeDisposable? = CompositeDisposable()
        disable?.add(GooglePlacesStreams.getListRestaurant(location, "5000", "restaurant", BuildConfig.api_key_google).subscribe(
                this::handleResponse, this::handleError))
    }

    fun handleResponse(nearbySearch: NearbySearch) {
        progressBar.visibility = View.GONE
        val listResults: List<Result> = nearbySearch.results
        val listViewAdapter = ListViewAdapter(listResults, requireActivity())
        recyclerView.adapter = listViewAdapter

    }

    private fun handleError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)
    }

}