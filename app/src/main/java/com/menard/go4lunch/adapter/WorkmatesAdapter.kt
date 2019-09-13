package com.menard.go4lunch.adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Typeface.DEFAULT
import android.graphics.Typeface.ITALIC
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.R
import com.menard.go4lunch.model.User
import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.detailsrequest.Result
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.utils.GooglePlacesStreams
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.disposables.CompositeDisposable

class WorkmatesAdapter(private val context:Context, options: FirestoreRecyclerOptions<User>) : FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkmatesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.workmates_list_item, parent, false)
        return WorkmatesViewHolder(view)
    }


    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: WorkmatesViewHolder, position: Int, user: User) {

        if(user.userPhoto != null){
            Glide.with(context).load(user.userPhoto).circleCrop().into(holder.userPhoto)
        }else{
            Glide.with(context).load(R.drawable.user).circleCrop().into(holder.userPhoto)
        }

        val restaurantChoosed: String
        if(user.userRestaurant != null) {
            restaurantChoosed = context.getString(R.string.workmates_infos_with_restaurant, user.userName, "TODO", user.userRestaurant)
            holder.userInfos.text = restaurantChoosed
        }else{
            holder.userInfos.setTextColor(Color.GRAY)
            holder.userInfos.setTypeface(DEFAULT, ITALIC)
            restaurantChoosed = user.userName + " hasn't decided yet"
            holder.userInfos.text = restaurantChoosed
        }

    }

    class WorkmatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userPhoto: CircleImageView = itemView.findViewById(R.id.image_profile)
        var userInfos: TextView = itemView.findViewById(R.id.workmates_infos)

    }

}