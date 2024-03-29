package com.menard.go4lunch.model.detailsrequest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Location (

    @SerializedName("lat")
    @Expose
    var lat: Double? = null,
    @SerializedName("lng")
    @Expose
    var lng: Double? = null
    )
