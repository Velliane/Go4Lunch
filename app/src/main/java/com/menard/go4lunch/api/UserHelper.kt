package com.menard.go4lunch.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.menard.go4lunch.model.Restaurant
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.model.User

/**
 * Contains the CRUD request for the Collection 'users' of Firestore
 */

class UserHelper {

    companion object {

        @JvmStatic
        fun getUsersCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS)
        }

        //-- CREATE USER --
        @JvmStatic
        fun createUser(userId: String, userName: String, userPhoto: String?, userRestaurantName: String?, userRestaurantId: String?, userLocationLatitude: String?, userLocationLongitude: String?): Task<Void> {
            val newUser = User(userId, userName, userPhoto, userRestaurantName, userRestaurantId, userLocationLatitude, userLocationLongitude)
            return getUsersCollection().document(userId).set(newUser)
        }

        //-- ADD RESTAURANT --
        @JvmStatic
        fun addFavorites(userId: String, placeId:String): Task<Void> {
            val favorite = Restaurant(placeId)
            return getUsersCollection().document(userId).collection(Constants.COLLECTION_FAVORITES_RESTAURANTS).document(placeId).set(favorite)
        }

        @JvmStatic
        fun deleteFavorites(userId: String, placeId: String):Task<Void>{
            return getUsersCollection().document(userId).collection(Constants.COLLECTION_FAVORITES_RESTAURANTS).document(placeId).delete()
        }

        //-- GET --
        @JvmStatic
        fun getUser(userId: String): Task<DocumentSnapshot> {
            return getUsersCollection().document(userId).get()
        }

        @JvmStatic
        fun getFavorites(userId: String): Task<QuerySnapshot> {
            return getUsersCollection().document(userId).collection(Constants.COLLECTION_FAVORITES_RESTAURANTS).get()
        }

        @JvmStatic
        fun getAllUser(): Query {
            return getUsersCollection().orderBy("userName")
        }

        @JvmStatic
        fun getUserAccordingToRestaurant(placeId: String): Query{
            return getUsersCollection().whereEqualTo("userRestaurantId", placeId)
        }
        //-- UPDATE --
        @JvmStatic
        fun updateName(userName: String, userId: String): Task<Void> {
            return getUsersCollection().document(userId).update("userName", userName)
        }

        @JvmStatic
        fun updateRestaurant(userId: String, userRestaurantName: String?, userRestaurantId: String? ): Task<Void> {
            return getUsersCollection().document(userId).update("userRestaurantName", userRestaurantName, "userRestaurantId", userRestaurantId)
        }

        @JvmStatic
        fun updateLocation(userId: String, userLocationLatitude: String?, userLocationLongitude: String?): Task<Void>{
            return getUsersCollection().document(userId).update("userLocationLatitude", userLocationLatitude, "userLocationLongitude", userLocationLongitude)
        }

        //-- DELETE USER --
        @JvmStatic
        fun deleteUser(userId: String): Task<Void> {
            return getUsersCollection().document(userId).delete()
        }


    }

}