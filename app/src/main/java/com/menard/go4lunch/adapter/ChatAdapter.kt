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
import com.menard.go4lunch.R
import com.menard.go4lunch.model.Message
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter (private val context: Context, options: FirestoreRecyclerOptions<Message>) : FirestoreRecyclerAdapter<Message, ChatAdapter.ChatViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, message: Message) {

        holder.message.text = message.message
        holder.date.text = message.date


    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userPhoto: CircleImageView = itemView.findViewById(R.id.image_profile)
        var message: TextView = itemView.findViewById(R.id.chat_item_message)
        var date: TextView = itemView.findViewById(R.id.chat_item_date)

    }
}