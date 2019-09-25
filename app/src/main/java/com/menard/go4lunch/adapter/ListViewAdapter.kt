package com.menard.go4lunch.adapter

import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.R
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.controller.activity.LunchActivity
import com.menard.go4lunch.model.User
import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.utils.Constants
import com.menard.go4lunch.utils.distanceToUser
import com.menard.go4lunch.utils.getProgressDrawableSpinner
import com.menard.go4lunch.utils.loadRestaurantPhoto

/**
 * Adapter for the RecyclerView that's show the detailed list of nearby restaurants
 */

class ListViewAdapter(val list: List<DetailsRequest>, private val context: Context) : RecyclerView.Adapter<ListViewAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listview_item, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val detailResult: DetailsRequest = list[position]
        val restaurant = detailResult.result

        holder.nameRestaurant.text = restaurant!!.name
        holder.styleAndAddress.text = restaurant.formattedAddress

        //-- Set opening hours --
        val opening: String = if (restaurant.openingHours != null) {
            if (restaurant.openingHours!!.openNow!!) {
                "Open"
            } else {
                "Close"
            }
        } else {
            "No opening hours available"
        }
        holder.openingHours.text = opening

        //-- Set distance according to user --
        val location = Location("")
        location.latitude = restaurant.geometry!!.location!!.lat!!
        location.longitude = restaurant.geometry!!.location!!.lng!!

        UserHelper.getUser(FirebaseAuth.getInstance().currentUser!!.uid).addOnSuccessListener { documentSnapshot ->
            val currentUser = documentSnapshot.toObject<User>(User::class.java)
            val latitude = currentUser?.userLocationLatitude!!.toDouble()
            val longitude = currentUser.userLocationLongitude!!.toDouble()
            val userLocation = Location("")
            userLocation.latitude = latitude
            userLocation.longitude = longitude
            holder.distance.text = distanceToUser(location, userLocation)
        }


        //-- Set restaurant's photo --
        if (restaurant.photos != null) {
            val reference: String? = restaurant.photos!![0].photoReference
            val url: String = context.getString(R.string.photos_list_view, reference, BuildConfig.api_key_google)
            holder.photo.loadRestaurantPhoto(url, null, getProgressDrawableSpinner(context))
        } else {
            holder.photo.loadRestaurantPhoto(null, R.drawable.no_image_available_64, getProgressDrawableSpinner(context))
        }

    }


    /**
     * View Holder
     */
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameRestaurant: TextView = itemView.findViewById(R.id.item_name_restaurant)
        var styleAndAddress: TextView = itemView.findViewById(R.id.item_address)
        var openingHours: TextView = itemView.findViewById(R.id.item_open_hours)
        var distance: TextView = itemView.findViewById(R.id.item_distance)
        var photo: ImageView = itemView.findViewById(R.id.item_photo)


        init {
            itemView.setOnClickListener { startLunchActivity() }
        }


        /**
         * Start LunchActivity to show details on the selected restaurant, according to place_id
         */
        private fun startLunchActivity() {
            val idRestaurant: String? = list[adapterPosition].result!!.placeId
            val intent = Intent(context, LunchActivity::class.java)
            intent.putExtra(Constants.EXTRA_RESTAURANT_IDENTIFIER, idRestaurant)
            context.startActivity(intent)
        }

    }
}