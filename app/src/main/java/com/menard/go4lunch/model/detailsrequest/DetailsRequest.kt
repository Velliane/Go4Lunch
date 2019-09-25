package com.menard.go4lunch.model.detailsrequest

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailsRequest(
    @SerializedName("result")
    @Expose
    var result: ResultDetails? = null
)
