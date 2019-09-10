package com.menard.go4lunch.controller.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.R
import com.menard.go4lunch.adapter.ListViewAdapter
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import com.menard.go4lunch.model.nearbysearch.Result
import com.menard.go4lunch.utils.GooglePlacesAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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


    fun listOfRestaurant() {

        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 2000
        locationRequest.smallestDisplacement = 50F
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val lastLocation: LatLng = onLocationChanged(locationResult.lastLocation)

                val retrofit = GooglePlacesAPI.retrofit
                val googlePlacesAPI = retrofit.create(GooglePlacesAPI::class.java)
                call?.cancel()
                call = googlePlacesAPI.getNearbySearch(lastLocation.latitude.toString() + "," + lastLocation.longitude.toString(), "2000", "restaurant", BuildConfig.api_key_google).also {


                    it.enqueue(object : Callback<NearbySearch> {

                        override fun onResponse(call: Call<NearbySearch>, response: Response<NearbySearch>) {

                            if(isAdded) {
                                progressBar.visibility = View.GONE
                                if (response.isSuccessful) {
                                    val nearbySearch = response.body()
                                    val listResults: List<Result> = nearbySearch!!.results
                                    val listViewAdapter = ListViewAdapter(listResults, requireActivity())
                                    recyclerView.adapter = listViewAdapter
                                }
                            }else{
                                it.cancel()
                            }
                        }

                        override fun onFailure(call: Call<NearbySearch>, t: Throwable) {
                            Log.e("Error", "Error")
                        }

                    })
                }
            }
        }, null)
    }


    override fun onStop() {
        super.onStop()
        if(call != null) {
            call?.cancel()
        }
    }

}