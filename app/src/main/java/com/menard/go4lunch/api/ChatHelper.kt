package com.menard.go4lunch.api

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.menard.go4lunch.utils.Constants

class ChatHelper {

    companion object{

        private fun getChatCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_MESSAGES)
        }

        fun getAllMessageForChat(): Query{
            return  getChatCollection().orderBy("date").limit(100)
        }

    }
}