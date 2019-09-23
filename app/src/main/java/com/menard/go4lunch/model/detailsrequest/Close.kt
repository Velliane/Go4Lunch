package com.menard.go4lunch.model.detailsrequest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Close (

    @SerializedName("day")
    @Expose
    var day: Int? = null,
    @SerializedName("time")
    @Expose
    var time: String? = null
)

