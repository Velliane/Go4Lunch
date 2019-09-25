package com.menard.go4lunch.utils

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.User
import java.util.*

fun distanceToUser(restaurantLocation: Location, userLocation: Location): String {
    val realDistance = userLocation.distanceTo(restaurantLocation)

    return if (realDistance >= 1000) {
        String.format(Locale.getDefault(), "%.2f", realDistance / 1000) + "km"
    } else {
        (realDistance.toInt().toString()) + "m"
    }
}

fun setRating(rating: Double): Int {
    return when {
        rating > 4.5 -> 3
        rating > 3.0 -> 2
        rating > 1.5 -> 1
        else -> 0
    }
}

fun setMarker(placeId: String, opening:String, googleMap:GoogleMap, latLng:LatLng, name:String){
    var number= 0
    UserHelper.getUsersCollection().get().addOnSuccessListener { result ->
        for (userId in result) {
            val user = userId.toObject(User::class.java)
            if ("${user.userRestaurant}" == name) {
                number++
            }
        }
        if (number == 0){
            googleMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_restaurant)).title(name).snippet(opening)).tag = placeId
        }else{
            googleMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected)).title(name).snippet(opening+" "+number+"workmates")).tag = placeId
        }
    }

}

