package com.menard.go4lunch.utils

import com.menard.go4lunch.model.nearbysearch.NearbySearch
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface GooglePlacesAPI {

    @GET("maps/api/place/nearbysearch/json")
    fun getNearbySearch(@Query("location") location: String, @Query("radius") radius: String, @Query("type") type: String, @Query("key") key: String): Call<NearbySearch>

    @GET("maps/api/place/nearbysearch/json")
    fun getNearbySearchRXJAVA(@Query("location") location: String, @Query("radius") radius: String, @Query("type") type: String, @Query("key") key: String): Observable<NearbySearch>

 companion object {
     val retrofit = Retrofit.Builder()
             .baseUrl("https://maps.googleapis.com/")
             .addConverterFactory(GsonConverterFactory.create())
             .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
             .build()
 }

}