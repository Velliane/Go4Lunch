package com.menard.go4lunch.adapter

import android.content.Context
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.menard.go4lunch.api.UserHelper
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.model.User
import com.menard.go4lunch.R
import com.menard.go4lunch.utils.parseMessageDateToDateOnly
import com.menard.go4lunch.utils.parseMessageDateToHoursOnly
import org.threeten.bp.LocalDateTime
import java.lang.IllegalStateException


class ChatAdapter (private val context: Context, options: FirestoreRecyclerOptions<Message>) : FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = when(viewType){
            0 -> inflater.inflate(R.layout.chat_item, parent, false)
            1 -> inflater.inflate(R.layout.chat_item_self, parent, false)
            else -> throw IllegalStateException()
        }

        return ChatViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val userId: String = FirebaseAuth.getInstance().currentUser!!.uid

        return if(userId == getItem(position).userId){
            1
        }else{
            0
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, message: Message) {

        holder.message.text = message.message

        //-- Date --
        val today: String = parseMessageDateToDateOnly(LocalDateTime.now().withNano(0).toString())
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
                Glide.with(context).load(photoUrl).into(holder.userPhoto)
            }else{
                Glide.with(context).load(R.drawable.user).into(holder.userPhoto)
            }
        }



    }


    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userPhoto: CircleImageView = itemView.findViewById(R.id.image_profile)
        var message: TextView = itemView.findViewById(R.id.chat_item_message)
        var date: TextView = itemView.findViewById(R.id.chat_item_date)
        var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.chat_item_constraint_layout)
    }


    /**
     * Set Image Profile to right for other's message
     */
    private fun setConstraintRight(constraintLayout: ConstraintLayout) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(R.id.image_profile, ConstraintSet.END, R.id.chat_item_constraint_layout, ConstraintSet.END)
        constraintSet.connect(R.id.chat_item_message, ConstraintSet.END, R.id.image_profile, ConstraintSet.START)
        constraintSet.connect(R.id.chat_item_date, ConstraintSet.END, R.id.image_profile, ConstraintSet.START)
        constraintSet.applyTo(constraintLayout)
    }

    /**
     * Set Image Profile to left for user's message
     */
    private fun setConstraintLeft(constraintLayout: ConstraintLayout){
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(R.id.image_profile, ConstraintSet.START, R.id.chat_item_constraint_layout, ConstraintSet.START)
        constraintSet.connect(R.id.chat_item_message, ConstraintSet.START, R.id.image_profile, ConstraintSet.END)
        constraintSet.connect(R.id.chat_item_date, ConstraintSet.START, R.id.image_profile, ConstraintSet.END)
        constraintSet.applyTo(constraintLayout)
    }
}