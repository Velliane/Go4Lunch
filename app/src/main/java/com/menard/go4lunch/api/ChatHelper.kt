package com.menard.go4lunch.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.menard.go4lunch.model.Message
import com.menard.go4lunch.utils.Constants

/**
 * Contains the CRUD request for the Collection 'chat' of Firestore
 */

class ChatHelper {

    companion object{

        private fun getChatCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_MESSAGES)
        }

        @JvmStatic
        fun getAllMessageForChat(): Query{
            return  getChatCollection().orderBy("date").limit(100)
        }

        @JvmStatic
        fun addMessage(date: String, message: String, userId: String): Task<Void> {
            val newMessage = Message(date, message, userId)
            return getChatCollection().document("$date-$userId").set(newMessage)
        }

    }
}