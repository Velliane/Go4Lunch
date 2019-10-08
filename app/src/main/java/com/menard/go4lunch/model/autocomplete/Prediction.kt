package com.menard.go4lunch.model.autocomplete

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Prediction (

        @SerializedName("description")
    @Expose
    var description: String? = null,
        @SerializedName("place_id")
    @Expose
    var placeId: String? = null,
        @SerializedName("structured_formatting")
    @Expose
    var structuredFormatting: StructuredFormatting? = null,
        @SerializedName("types")
    @Expose
    var types: List<String>? = null

    )
