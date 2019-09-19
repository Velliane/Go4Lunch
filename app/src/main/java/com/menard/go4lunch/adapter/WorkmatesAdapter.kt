package com.menard.go4lunch.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface.DEFAULT
import android.graphics.Typeface.ITALIC
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.R
import com.menard.go4lunch.model.User
import de.hdodenhof.circleimageview.CircleImageView

class WorkmatesAdapter(private val context:Context, options: FirestoreRecyclerOptions<User>) : FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkmatesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.workmates_item, parent, false)
        return WorkmatesViewHolder(view)
    }


    override fun onBindViewHolder(holder: WorkmatesViewHolder, position: Int, user: User) {

        //-- Hide item if it's current user's information --
        if(user.userId == FirebaseAuth.getInstance().currentUser!!.uid){
            val param: RecyclerView.LayoutParams = holder.container.layoutParams as RecyclerView.LayoutParams
            param.height = 0
            holder.itemView.visibility = View.VISIBLE

        //-- Show the other users's information --
        }else{

            if(user.userPhoto != null){
                Glide.with(context).load(user.userPhoto).circleCrop().into(holder.userPhoto)
            }else{
                Glide.with(context).load(R.drawable.user).circleCrop().into(holder.userPhoto)
            }
            val restaurantChoosed: String
            if(user.userRestaurant != null) {
                restaurantChoosed = context.getString(R.string.workmates_infos_with_restaurant, user.userName, "TODO", user.userRestaurant)
                holder.userInfos.text = restaurantChoosed
                holder.userInfos.setTextColor(Color.BLACK)
                holder.userInfos.typeface = DEFAULT
            }else{
                holder.userInfos.setTextColor(Color.GRAY)
                holder.userInfos.setTypeface(DEFAULT, ITALIC)
                restaurantChoosed = user.userName + " hasn't decided yet"
                holder.userInfos.text = restaurantChoosed
            }
        }

    }

    class WorkmatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userPhoto: CircleImageView = itemView.findViewById(R.id.image_profile)
        var userInfos: TextView = itemView.findViewById(R.id.workmates_infos)
        var container: ConstraintLayout = itemView.findViewById(R.id.workmates_item_container)

    }

}