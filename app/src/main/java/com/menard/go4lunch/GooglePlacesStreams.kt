package com.menard.go4lunch

import com.menard.go4lunch.model.nearbysearch.NearbySearch
import com.menard.go4lunch.utils.GooglePlacesAPI
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GooglePlacesStreams {

    companion object{

        fun getListRestaurant(location: String, radius: String, restaurant: String, apiKey: String ): Observable<NearbySearch> {
            val retrofit = GooglePlacesAPI.retrofit.create(GooglePlacesAPI::class.java)
            return  retrofit.getNearbySearchRXJAVA(location, radius, restaurant, apiKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .timeout(10, TimeUnit.SECONDS)
        }

        fun getDetails(id: String){

        }
    }

}