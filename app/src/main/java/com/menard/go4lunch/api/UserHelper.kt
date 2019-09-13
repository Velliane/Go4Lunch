package com.menard.go4lunch.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.model.User

class UserHelper {

    companion object {

        private fun getUsersCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS)
        }

        //-- CREATE USER --
        fun createUser(userId: String, userName: String, userPhoto: String?, userRestaurant: String?): Task<Void> {
            val newUser = User(userId, userName, userPhoto, userRestaurant)
            return getUsersCollection().document(userId).set(newUser)
        }

        //-- GET USER --
        fun getUser(userId: String): Task<DocumentSnapshot> {
            return getUsersCollection().document(userId).get()
        }
//        fun getUserName(userId: String): Query {
//            return getUsersCollection().whereEqualTo("userId", userId)
//        }

        //-- GET ALL USERS --
        fun getAllUser(): Query {
            return getUsersCollection().orderBy("userName")
        }

        //-- UPDATE --
        fun updateName(userName: String, userId: String): Task<Void> {
            return getUsersCollection().document(userId).update("userName", userName)
        }

        fun udpateRestaurant(userId: String, userRestaurant: String?): Task<Void> {
            return getUsersCollection().document(userId).update("userRestaurant", userRestaurant)
        }

        //-- DELETE USER --
        fun deleteUser(userId: String): Task<Void> {
            return getUsersCollection().document(userId).delete()
        }


    }

}