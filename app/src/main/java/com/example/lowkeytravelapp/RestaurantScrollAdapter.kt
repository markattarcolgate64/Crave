package com.example.lowkeytravelapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RestaurantScrollAdapter(private val restaurants: ArrayList<Restaurant>, private val interactionListener: RestaurantListInterface
) :
    RecyclerView.Adapter<RestaurantScrollAdapter.ViewHolder>() {
//    private var spots: List<Spot> = emptyList()




    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameview: TextView
        val imageView: ImageView
        init {
            // Define click listener for the ViewHolder's View
            nameview = view.findViewById(R.id.Rname)
            imageView = view.findViewById(R.id.Rimage)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.fragment_restaurant_card, parent, false))
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.nameview.text = restaurant.name
        val url = restaurant.imageUrl
        println("restaurant url $url")
        // Load image using Glide
        Glide.with(holder.itemView)
            .load(restaurant.imageUrl)
//            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
            .error(R.drawable.error_image) // Image to display in case of error
            .into(holder.imageView)

        holder.itemView.setOnClickListener { v ->
           // Toast.makeText(v.context, restaurant.name, Toast.LENGTH_SHORT).show()
            interactionListener.onFoodItemClick(restaurant)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = restaurants.size

}
