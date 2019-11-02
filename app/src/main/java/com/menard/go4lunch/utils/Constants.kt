package com.menard.go4lunch.utils

class Constants{

    companion object{
        //-- Request code for sign in --
        const val REQUEST_CODE_SIGN_IN = 240
        //-- Request code for permission for fine location --
        const val REQUEST_CODE_UPDATE_LOCATION = 4
        //-- Request code for permission for phone call --
        const val REQUEST_CODE_CALL_PHONE = 3


        //-- Collection --
        const val COLLECTION_USERS = "users"
        const val COLLECTION_MESSAGES = "chat"
        const val COLLECTION_FAVORITES_RESTAURANTS = "favorites"

        //-- Request --
        const val FIELD_FOR_DETAILS = "place_id,geometry,formatted_address,name,rating,permanently_closed,photo,type,formatted_phone_number,opening_hours,website"
        const val FIELD_FOR_RADIUS = "7000"
        const val FIELD_FOR_TYPE = "restaurant"

        //-- Extra for Intent --
        const val EXTRA_RESTAURANT_IDENTIFIER = "EXTRA_RESTAURANT_IDENTIFIER"

        //-- Shared Preferences --
        const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
        const val PREF_RESTAURANT_SELECTED = "PREF_RESTAURANT_SELECTED"
        const val PREF_NOTIFICATIONS_HOURS = "PREF_NOTIFICATIONS_HOURS"
        const val PREF_NOTIFICATIONS_MINUTES = "PREF_NOTIFICATIONS_MINUTES"
        const val PREF_ENABLED_NOTIFICATIONS = "PREF_ENABLED_NOTIFICATIONS"

        //-- Data for notifications --
        const val DATA_USER = "DATA_USER_ID"
        const val DATA_RESTAURANT_NAME = "DATA_RESTAURANT_NAME"
        const val DATA_RESTAURANT_ADDRESS = "DATA_RESTAURANT_ADDRESS"
        const val DATA_RESTAURANT_ID = "DATA_RESTAURANT_ID"

    }
}