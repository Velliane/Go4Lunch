package com.menard.go4lunch.model.autocomplete

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Autocomplete(

        @SerializedName("predictions")
        @Expose
        var predictions: List<Prediction>? = null

)
