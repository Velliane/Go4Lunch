package com.menard.go4lunch.utils

import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GooglePlacesStreams {

    companion object{

        //-- Get Nearby Restaurant --
        fun getListRestaurant(location: String, radius: String, restaurant: String, apiKey: String ): Observable<NearbySearch> {
            val retrofit = GooglePlacesAPI.retrofit.create(GooglePlacesAPI::class.java)
            return  retrofit.getNearbySearchRXJAVA(location, radius, restaurant, apiKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .timeout(10, TimeUnit.SECONDS)
        }

        //-- Get Details of Restaurant according to id --
        fun getDetails(id: String, fields: String, apiKey: String): Observable<DetailsRequest>{
            val retrofit = GooglePlacesAPI.retrofit.create(GooglePlacesAPI::class.java)
            return retrofit.getDetails(id, fields, apiKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .timeout(10, TimeUnit.SECONDS)
        }

//        fun getListId(location: String, radius: String, restaurant: String, apiKey: String): Observable<List<String>>{
//            return getListRestaurant(location, radius, restaurant, apiKey)
//                    .map { t: NearbySearch ->
//
//                        val listId: ArrayList<String> = ArrayList()
//                        if(t.results.size != 0){
//                            for(result in t.results){
//                                listId.add(result.placeId)
//                            }
//                        }
//                        return@map listId
//                    }
//        }

        fun getDetailsOfSelectedRestaurant(location: String, radius: String, restaurant: String, fields: String, apiKey: String): Observable<List<DetailsRequest>>{
            return getListRestaurant(location, radius, restaurant, apiKey)
                    .concatMapIterable { result -> result.results }
                    .concatMap<DetailsRequest> { detail -> getDetails(id = detail.placeId, fields = fields, apiKey = apiKey) }
                    .toList().toObservable()
            }


        }

        //-- Chain request --
//        fun getDetailsOfSelectedRestaurant(location: String, radius: String, restaurant: String, fields: String, apiKey: String): Observable<DetailsRequest> {
//
//            return getListId(location, radius, restaurant, apiKey)
//                    .flatMap { list ->
//                        getDetails(list, fields, apiKey)
//                    }
//          }




//                    .flatMap(object : Function<NearbySearch, DetailsRequest> {
//                        return@Function it.results[]
//                    }.also {  }

//            return getDetails(id, fields, apiKey)
//                    .flatMap { t: DetailsRequest ->
//                        getListRestaurant(location, radius, restaurant, apiKey)
//                    }




}