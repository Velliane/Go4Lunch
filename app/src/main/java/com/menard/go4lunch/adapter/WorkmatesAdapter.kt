package com.menard.go4lunch.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.menard.go4lunch.R
import com.menard.go4lunch.model.User
import de.hdodenhof.circleimageview.CircleImageView

class WorkmatesAdapter(private val context:Context, options: FirestoreRecyclerOptions<User>) : FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder>(options) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkmatesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.workmates_list_item, parent, false)
        return WorkmatesViewHolder(view)
    }


    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: WorkmatesViewHolder, position: Int, user: User) {

        if(user.userPhoto != null){
            Glide.with(context).load(user.userPhoto).into(holder.userPhoto)
        }else{
            Glide.with(context).load(R.drawable.user).into(holder.userPhoto)
        }

        val string = context.getString(R.string.workmates_infos_with_restaurant, user.userName, user.userId)
        holder.userInfos.text = string


    }


    class WorkmatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userPhoto: CircleImageView
        var userInfos: TextView

        init {
            userPhoto = itemView.findViewById(R.id.image_profile)
            userInfos = itemView.findViewById(R.id.workmates_infos)

        }
    }
}