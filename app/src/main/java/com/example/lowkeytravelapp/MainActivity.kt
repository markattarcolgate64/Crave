package com.example.lowkeytravelapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity(), YumInterface, OnPlacesReadyCallback {

    private val placeFinder = PlaceFinder()
    override fun onYumButtonClick(query: String) {
        // Call a method in MapsFragment to perform the location lookup
        val mapsFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as MapsFragment
        val location = mapsFragment.getLastKnownLocation()
        // Example usage:
        val keyword = query
        val radius = 100 // Radius in meters
        val latitude = location!!.latitude
        // Example latitude
        val longitude = location.longitude // Example longitude

        // Call the searchPlaces method to initiate the search
        placeFinder.searchPlaces(keyword, radius, latitude, longitude)
    }

    override fun onPlacesReady(placesList: RestaurantList) {

        val mapFrag = MapsFragment()
        val mapArgs = Bundle()
        mapArgs.putParcelable("restaurantsList", placesList)
        mapFrag.arguments = mapArgs
        //val mapsFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as MapsFragment
        replaceFragment(R.id.fragment_container, mapFrag)
    }

    override fun onError(message: String) {
        TODO("Not yet implemented")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        println("MAIN ACTIVITY")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        //Loads in FoodDisplay fragment
        //The restaurant data is passed to the maps fragment
        val food = "chow main"
        val FoodFrag = FoodDisplayFragment()
        val foodArgs = Bundle()
        foodArgs.putString("food", food)
        FoodFrag.arguments = foodArgs
        replaceFragment(R.id.fragment_container, FoodFrag)


    }

    private fun replaceFragment(containerId: Int, replacement: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, replacement)
            .commit()
    }


}


