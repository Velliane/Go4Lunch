package com.menard.go4lunch.utils

import com.menard.go4lunch.model.nearbysearch.NearbySearch
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesAPI {

    @GET("maps/api/place/nearbysearch/json")
    fun getNearbySearch(@Query("location") location: String, @Query("radius") radius: String, @Query("type") type: String, @Query("key") key: String): Call<NearbySearch>


 companion object {
     val retrofit = Retrofit.Builder()
             .baseUrl("https://maps.googleapis.com/")
             .addConverterFactory(GsonConverterFactory.create())
             .build()
 }

}