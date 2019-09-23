package com.menard.go4lunch.utils

import android.content.Context
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Add progress drawable
 */
fun getProgressDrawableSpinner(context: Context): CircularProgressDrawable{
    return CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 40f
        start()
    }
}

/**
 * Load the profile's image of user
 */
fun CircleImageView.loadImageProfile(url: String?, progressDrawable: CircularProgressDrawable){
    val options = RequestOptions().placeholder(progressDrawable)

    Glide.with(this.context).setDefaultRequestOptions(options).load(url).into(this)
}

/**
 * Load restaurant's photo
 */
fun ImageView.loadRestaurantPhoto(@Nullable url: String?, @Nullable int:Int?, progressDrawable: CircularProgressDrawable){
    val options = RequestOptions().placeholder(progressDrawable)

    if(url != null) {
        Glide.with(this.context).setDefaultRequestOptions(options.centerCrop()).load(url).into(this)
    }else{
        Glide.with(this.context).setDefaultRequestOptions(options).load(int).into(this)
    }
}

