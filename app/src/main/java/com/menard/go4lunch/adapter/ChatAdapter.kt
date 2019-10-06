package com.menard.go4lunch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.Message
import com.menard.go4lunch.model.User
import com.menard.go4lunch.utils.getProgressDrawableSpinner
import com.menard.go4lunch.utils.loadImageProfile
import com.menard.go4lunch.utils.parseMessageDateToDateOnly
import com.menard.go4lunch.utils.parseMessageDateToHoursOnly
import de.hdodenhof.circleimageview.CircleImageView
import org.threeten.bp.LocalDateTime

/**
 * Adapter for the RecyclerView that's show the chat between users and their messages got from Firebase,
 * using FirestoreRecyclerAdapter and FirestoreRecyclerOptions
 */

class ChatAdapter (private val context: Context, options: FirestoreRecyclerOptions<Message>) : FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //-- Add two ViewType --
        val view = when(viewType){
            0 -> inflater.inflate(R.layout.chat_item, parent, false)
            1 -> inflater.inflate(R.layout.chat_item_self, parent, false)
            else -> throw IllegalStateException()
        }
        return ChatViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        //-- Select ViewType according to userId --
        val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
        return if(userId == getItem(position).userId) 1 else 0
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, message: Message) {

        //-- Message --
        holder.message.text = message.message

        //-- Date parsed --
        val today: String = parseMessageDateToDateOnly(LocalDateTime.now().withSecond(2).withNano(0).toString())
        val dateMessage: String = parseMessageDateToDateOnly(message.date)
        if(today != dateMessage){
            holder.date.text = dateMessage
        }else{
            holder.date.text = parseMessageDateToHoursOnly(message.date)
        }

        //-- Profile's photo
        UserHelper.getUser(message.userId).addOnSuccessListener { documentSnapshot ->
            val currentUser = documentSnapshot.toObject<User>(User::class.java)
            val photoUrl = currentUser?.userPhoto
            if(photoUrl != null) {
                holder.userPhoto.loadImageProfile(photoUrl, null, getProgressDrawableSpinner(context))
            }else{
                holder.userPhoto.loadImageProfile(null, R.drawable.user, getProgressDrawableSpinner(context))
            }
        }
    }


    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userPhoto: CircleImageView = itemView.findViewById(R.id.image_profile)
        var message: TextView = itemView.findViewById(R.id.chat_item_message)
        var date: TextView = itemView.findViewById(R.id.chat_item_date)
    }


}