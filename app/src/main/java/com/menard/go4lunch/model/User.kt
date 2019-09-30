package com.menard.go4lunch.model

data class User(
        val userId: String = "",
        val userName: String = "",
        val userPhoto: String? = "",
        val userRestaurantName: String? = "",
        val userRestaurantId: String? = "",
        val userLocationLatitude: String? = "",
        val userLocationLongitude: String? = ""
)

