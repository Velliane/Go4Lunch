package com.menard.go4lunch.model.autocomplete

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StructuredFormatting (

    @SerializedName("main_text")
    @Expose
    var mainText: String? = null,
    @SerializedName("secondary_text")
    @Expose
    var secondaryText: String? = null

)
