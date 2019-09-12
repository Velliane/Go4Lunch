package com.menard.go4lunch.utils

class Constants{
    companion object{
        //-- Request code for sign in --
        const val RC_SIGN_IN = 240
        //-- Request code for permission for fine location --
        const val REQUEST_CODE_UPDATE_LOCATION = 4
        //-- Request code for autocomplete --
        const val REQUEST_CODE_AUTOCOMPLETE = 1


        //-- Collection of Users --
        const val COLLECTION_USERS = "users"

        //-- Request --
        const val FIELD_FOR_DETAILS = "formatted_address,name,rating,permanently_closed,photo,type,formatted_phone_number,opening_hours,website"

        //-- Extra for Intent --
        const val EXTRA_RESTAURANT_IDENTIFIER = "EXTRA_RESTAURANT_IDENTIFIER"
    }
}