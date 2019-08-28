package com.menard.go4lunch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.menard.go4lunch.R

class ListViewAdapter : RecyclerView.Adapter<ListViewAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_view_item, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.nameRestaurant.text = "Le Restaurant du coin"
        holder.styleAndAddress.text = "Français - 14 rue du quartier"
        holder.openingHours.text = "Ouvert jusqu'à 22h"
        holder.distance.text = "250m"

    }


    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameRestaurant: TextView
        var styleAndAddress: TextView
        var openingHours: TextView
        var distance: TextView

        init {
            nameRestaurant = itemView.findViewById(R.id.item_name_restaurant)
            styleAndAddress = itemView.findViewById(R.id.item_address)
            openingHours = itemView.findViewById(R.id.item_open_hours)
            distance = itemView.findViewById(R.id.item_distance)
        }
    }
}