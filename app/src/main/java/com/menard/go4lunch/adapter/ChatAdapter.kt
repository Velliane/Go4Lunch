package com.menard.go4lunch.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.menard.go4lunch.model.Message
import de.hdodenhof.circleimageview.CircleImageView
import android.text.TextUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.OnSuccessListener
import com.menard.go4lunch.api.UserHelper
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.model.User
import com.menard.go4lunch.R


class ChatAdapter (private val context: Context, options: FirestoreRecyclerOptions<Message>) : FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, message: Message) {

        holder.message.text = message.message
        holder.date.text = message.date


        UserHelper.getUser(message.userId).addOnSuccessListener { documentSnapshot ->
            val currentUser = documentSnapshot.toObject<User>(User::class.java)
            val photoUrl = currentUser?.userPhoto

            if(photoUrl != null) {
                Glide.with(context).load(photoUrl).into(holder.userPhoto)
            }else{
                Glide.with(context).load(R.drawable.user).into(holder.userPhoto)
            }
        }

        //-- Update UI according to the user connected --
        if(message.userId == FirebaseAuth.getInstance().currentUser!!.uid){
            holder.message.setBackgroundResource(R.drawable.chat_current_user_message_background)
            val constraintSet = ConstraintSet()
            constraintSet.clone(holder.constraintLayout)
            constraintSet.connect(R.id.image_profile, ConstraintSet.START, R.id.chat_item_constraint_layout, ConstraintSet.START)
            constraintSet.connect(R.id.chat_item_message, ConstraintSet.START, R.id.image_profile, ConstraintSet.END)
            constraintSet.connect(R.id.chat_item_date, ConstraintSet.START, R.id.image_profile, ConstraintSet.END)
            constraintSet.applyTo(holder.constraintLayout)
        }else{
            holder.message.setBackgroundResource(R.drawable.chat_message_background)
            val constraintSet = ConstraintSet()
            constraintSet.clone(holder.constraintLayout)
            constraintSet.connect(R.id.image_profile, ConstraintSet.END, R.id.chat_item_constraint_layout, ConstraintSet.END)
            constraintSet.connect(R.id.chat_item_message, ConstraintSet.END, R.id.image_profile, ConstraintSet.START)
            constraintSet.connect(R.id.chat_item_date, ConstraintSet.END, R.id.image_profile, ConstraintSet.START)
            constraintSet.applyTo(holder.constraintLayout)
        }

    }


    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userPhoto: CircleImageView = itemView.findViewById(R.id.image_profile)
        var message: TextView = itemView.findViewById(R.id.chat_item_message)
        var date: TextView = itemView.findViewById(R.id.chat_item_date)
        var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.chat_item_constraint_layout)

    }
}