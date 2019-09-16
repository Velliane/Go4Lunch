package com.menard.go4lunch.utils

class Constants{
    companion object{
        //-- Request code for sign in --
        const val RC_SIGN_IN = 240
        //-- Request code for permission for fine location --
        const val REQUEST_CODE_UPDATE_LOCATION = 4
        //-- Request code for autocomplete --
        const val REQUEST_CODE_AUTOCOMPLETE = 1


        //-- Collection --
        const val COLLECTION_USERS = "users"
        const val COLLECTION_MESSAGES = "chat"

        //-- Request --
        const val FIELD_FOR_DETAILS = "formatted_address,name,rating,permanently_closed,photo,type,formatted_phone_number,opening_hours,website"

        //-- Extra for Intent --
        const val EXTRA_RESTAURANT_IDENTIFIER = "EXTRA_RESTAURANT_IDENTIFIER"

        //-- Shared Preferences --
        const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
        const val PREF_RESTAURANT_SELECTED = "PREF_RESTAURANT_SELECTED"
    }
}