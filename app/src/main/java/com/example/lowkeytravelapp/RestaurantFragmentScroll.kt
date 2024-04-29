package com.example.lowkeytravelapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RestaurantFragmentScroll : Fragment(), RestaurantListInterface{

    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: RestaurantScrollAdapter
    private lateinit var restaurantsglobal: ArrayList<Restaurant>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_scroll, container, false)

        val restaurantList = arguments?.getParcelable("restaurantsList", RestaurantList::class.java)
        val restaurants: ArrayList<Restaurant> = restaurantList!!.restaurants
        restaurantsglobal = restaurants
        recyclerview = view.findViewById(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = RestaurantScrollAdapter(getSampleRestaurantData(), this)
        recyclerview.adapter = adapter

        return view
    }

    override fun onFoodItemClick(restaurant: Restaurant) {
        Toast.makeText(requireActivity(), "onFoodItemClick interface working", Toast.LENGTH_SHORT).show()
        val clickBundle = Bundle()
        clickBundle.putParcelable("restaurant",restaurant)
        requireActivity().supportFragmentManager.setFragmentResult("restaurantClick", clickBundle)

    }


    private fun getSampleRestaurantData(): ArrayList<Restaurant> {
    // Sample restaurant data
//        val list = listOf(
//            "Restaurant 1",
//            "Restaurant 2",
//            "Restaurant 3",
//            "Restaurant 4",
//            "Restaurant 5"
//        )
//
//        val retlist: ArrayList<Restaurant> = ArrayList()
//
//        for (name in list) {
//            retlist.add(Restaurant(name,40.71883,-73.988281,"https://www.recipetineats.com/wp-content/uploads/2023/11/Avocado-salad_0.jpg"))
//        }

        return restaurantsglobal
    }

}
