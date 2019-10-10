package com.menard.go4lunch.model.detailsrequest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultDetails (

    @SerializedName("place_id")
    @Expose
    var placeId: String? = null,
    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String? = null,
    @SerializedName("formatted_phone_number")
    @Expose
    var formattedPhoneNumber: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("opening_hours")
    @Expose
    var openingHours: OpeningHours? = null,
    @SerializedName("photos")
    @Expose
    var photos: List<Photo>? = null,
    @SerializedName("rating")
    @Expose
    var rating: Double? = null,
    @SerializedName("website")
    @Expose
    var website: String? = null,
    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null
    )
