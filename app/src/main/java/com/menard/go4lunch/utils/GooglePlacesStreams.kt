package com.menard.go4lunch.utils

import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GooglePlacesStreams {

    companion object {

        //-- Get Nearby Restaurant --
        @JvmStatic
        fun getListRestaurant(location: String, radius: String, restaurant: String, apiKey: String): Observable<NearbySearch> {
            val retrofit = GooglePlacesAPI.retrofit.create(GooglePlacesAPI::class.java)
            return retrofit.getNearbySearch(location, radius, restaurant, apiKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .timeout(10, TimeUnit.SECONDS)
        }

        //-- Get Details of Restaurant according to id --
        @JvmStatic
        fun getDetails(id: String, fields: String, apiKey: String): Observable<DetailsRequest> {
            val retrofit = GooglePlacesAPI.retrofit.create(GooglePlacesAPI::class.java)
            return retrofit.getDetails(id, fields, apiKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .timeout(10, TimeUnit.SECONDS)
        }

        //-- Get List of Details --
        @JvmStatic
        fun getDetailsOfSelectedRestaurant(location: String, radius: String, restaurant: String, fields: String, apiKey: String): Observable<List<DetailsRequest>> {
            return getListRestaurant(location, radius, restaurant, apiKey)
                    .concatMapIterable { result -> result.results }
                    .concatMap<DetailsRequest> { detail -> getDetails(id = detail.placeId, fields = fields, apiKey = apiKey) }
                    .toList().toObservable()
        }

    }




}