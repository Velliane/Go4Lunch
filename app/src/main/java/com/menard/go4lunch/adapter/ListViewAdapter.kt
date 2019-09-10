package com.menard.go4lunch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.menard.go4lunch.R
import com.menard.go4lunch.model.nearbysearch.Result

class ListViewAdapter(private val list: List<Result>, private val context:Context) : RecyclerView.Adapter<ListViewAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_view_item, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val restaurant: Result = list[position]

        holder.nameRestaurant.text = restaurant.name
        holder.styleAndAddress.text = restaurant.vicinity

        val opening: String = if (restaurant.openingHours != null) {
            if(restaurant.openingHours.openNow){
                "Open"
            }else{
                "Close"
            }
        } else {
            "No opening hours available"
        }
        holder.openingHours.text = opening


        holder.distance.text = "200m"

        if (restaurant.photos != null) {
            val photo = "https://www.google.com/maps/contrib/103887203559295854657/photos/" + restaurant.photos[0].photoReference
            Glide.with(context).load(photo).into(holder.photo)
        }else{
            TODO()
        }

    }


    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameRestaurant: TextView = itemView.findViewById(R.id.item_name_restaurant)
        var styleAndAddress: TextView = itemView.findViewById(R.id.item_address)
        var openingHours: TextView = itemView.findViewById(R.id.item_open_hours)
        var distance: TextView = itemView.findViewById(R.id.item_distance)
        var photo: ImageView = itemView.findViewById(R.id.item_photo)

    }
}