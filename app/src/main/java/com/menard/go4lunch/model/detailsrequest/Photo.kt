package com.menard.go4lunch.model.detailsrequest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Photo (

    @SerializedName("height")
    @Expose
    var height: Int? = null,
    @SerializedName("photo_reference")
    @Expose
    var photoReference: String? = null,
    @SerializedName("width")
    @Expose
    var width: Int? = null

    )