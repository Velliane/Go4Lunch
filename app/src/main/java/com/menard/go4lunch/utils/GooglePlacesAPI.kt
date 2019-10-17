package com.menard.go4lunch.utils

import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.nearbysearch.NearbySearch
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesAPI {

    @GET("maps/api/place/nearbysearch/json")
    fun getNearbySearch(@Query("location") location: String, @Query("radius") radius: String, @Query("type") type: String, @Query("key") key: String): Observable<NearbySearch>

    @GET("maps/api/place/details/json")
    fun getDetails(@Query("placeid")placeId: String, @Query ("fields") fields: String, @Query("key") key:String): Observable<DetailsRequest>

//    @GET("maps/api/place/autocomplete/json?strictbounds&types=establishment")
//    fun getAutocompleteSearch(@Query("input")input: String, @Query("location") location: String, @Query("radius") radius: String, @Query("key") key: String): Observable<Autocomplete>


 companion object {
     val retrofit: Retrofit = Retrofit.Builder()
             .baseUrl("https://maps.googleapis.com/")
             .addConverterFactory(GsonConverterFactory.create())
             .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
             .build()
 }

}