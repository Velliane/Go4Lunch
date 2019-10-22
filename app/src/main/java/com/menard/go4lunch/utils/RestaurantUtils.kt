package com.menard.go4lunch.utils

import android.content.Context
import android.location.Location
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.User
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
fun setMarker(number: Int, placeId: String, opening: String, googleMap: GoogleMap, latLng: LatLng, name: String, context: Context): Marker {

    val marker: Marker
    if (number == 0) {
        val markerOptions = MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_restaurant)).title(name).snippet(opening)
        marker = googleMap.addMarker(markerOptions)
        marker.tag = placeId
        return marker
    } else {
        val markerOptions = MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected)).title(name).snippet(context.getString(R.string.selected_restaurant, opening, number))
        marker = googleMap.addMarker(markerOptions)
        marker.tag = placeId
        return marker
    }

}


fun getNumberOfWorkmates(name: String, textView: TextView) {
    var number = 0
    UserHelper.getUsersCollection().get().addOnSuccessListener { result ->
        for (userId in result) {
            val user = userId.toObject(User::class.java)
            if ("${user.userRestaurantName}" == name) {
                number++
            }
            textView.text = (number.toString())
        }
    }
}


/**
 * Get hours of closing according to the number of day
 */
fun getOpeningHours(day: Int, list: List<String>, context: Context): String {
    val hours = list[day].substringAfter(":")
    return if (hours.contains("Closed") || hours.contains("Fermé")) {
        context.getString(R.string.restaurant_closed_all_day)
    } else {
        hours
    }

}

