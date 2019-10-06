package com.menard.go4lunch.utils

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.User
import com.menard.go4lunch.model.detailsrequest.Period
import com.menard.go4lunch.model.detailsrequest.ResultDetails
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

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

/**
 * Change the color of the marker
 * Orange if no workmate have selected it
 * Green if at list one have selected it
 */
fun setMarker(placeId: String, opening:String, googleMap:GoogleMap, latLng:LatLng, name:String){
    var number= 0
    UserHelper.getUsersCollection().get().addOnSuccessListener { result ->
        for (userId in result) {
            val user = userId.toObject(User::class.java)
            if ("${user.userRestaurantName}" == name) {
                number++
            }
        }
        if (number == 0){
            googleMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_restaurant)).title(name).snippet(opening)).tag = placeId
        }else{
            googleMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected)).title(name).snippet("$opening $number workmates")).tag = placeId
        }
    }

}

fun getListOfWorkmates(placeId: String): String {
    val list: ArrayList<String> = ArrayList()
    val stringBuilder = StringBuilder()
    UserHelper.getUsersCollection().get().addOnSuccessListener { result ->
        for(userId in result){
            val user = userId.toObject(User::class.java)
            if (user.userRestaurantId == placeId) {
                list.add(user.userName)
                stringBuilder.append(user.userName+",")
            }
        }
    }

    return stringBuilder.toString()
}

/**
 * Get hours of closing according to the number of day
 */
fun getClosingTimeOfDay(isOpen: Boolean, today: Int, periods: List<Period>, hours: String): String {
    val closingTime = StringBuilder()
    // Iterate in list of Period
    for (period in periods) {

        // If closed
        if (!isOpen) {
            if(period.open?.day == today) {
                val time = period.open!!.time!!.toInt()
                if(time > hours.toInt()) {
                    var date = "today"
                    val timeParsed = parsePeriodHoursToHours(period.open!!.time!!)
                    closingTime.append("Closed, will open $date at $timeParsed")
                }
            }
            // If open
        } else {
            if(period.close!!.day == today) {
                val time = period.close!!.time!!.toInt()
                if(time > hours.toInt()) {
                    val timeParsed = parsePeriodHoursToHours(period.close!!.time!!)
                    closingTime.append("Open, will close at $timeParsed")
                }
            }
        }
    }
    return closingTime.toString()
}

