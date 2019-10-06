package com.menard.go4lunch.utils

class Constants{
    companion object{
        //-- Request code for sign in --
        const val REQUEST_CODE_SIGN_IN = 240
        //-- Request code for permission for fine location --
        const val REQUEST_CODE_UPDATE_LOCATION = 4
        //-- Request code for call phone --
        const val REQUEST_CODE_CALL_PHONE = 3
        //-- Request code for autocomplete --
        const val REQUEST_CODE_AUTOCOMPLETE = 1


        //-- Collection --
        const val COLLECTION_USERS = "users"
        const val COLLECTION_MESSAGES = "chat"
        const val COLLECTION_FAVORITES_RESTAURANTS = "favorites"

        //-- Request --
        const val FIELD_FOR_DETAILS = "place_id,geometry,formatted_address,name,rating,permanently_closed,photo,type,formatted_phone_number,opening_hours,website"

        //-- Extra for Intent --
        const val EXTRA_RESTAURANT_IDENTIFIER = "EXTRA_RESTAURANT_IDENTIFIER"

        //-- Shared Preferences --
        const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
        const val PREF_RESTAURANT_SELECTED = "PREF_RESTAURANT_SELECTED"
        const val PREF_NOTIFICATIONS_TIME = "PREF_NOTIFICATIONS_TIME"
        const val PREF_ENABLED_NOTIFICATIONS = "PREF_ENABLED_NOTIFICATIONS"

        //-- Data --
        const val DATA_USER = "DATA_USER_ID"
        const val DATA_RESTAURANT_NAME = "DATA_RESTAURANT_NAME"
        const val DATA_RESTAURANT_ADDRESS = "DATA_RESTAURANT_ADDRESS"
        const val DATA_LIST_WORKMATES = "DATA_LIST_WORKMATES"
        const val DATA_RESTAURANT_ID = "DATA_RESTAURANT_ID"

    }
}