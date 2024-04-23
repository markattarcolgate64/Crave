package com.example.lowkeytravelapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), YumInterface, OnPlacesReadyCallback {

    //private lateinit var viewModel: PlaceFinder

    override fun onYumButtonClick(query: String) {
        // Call a method in MapsFragment to perform the location lookup
//        val mapsFragment = MapsFragment()
//        val location = mapsFragment.getLastKnownLocation()
        // Example usage:
        val keyword = query
        val radius = 100 // Radius in meters
        val latitude = 40.713713//location!!.latitude
        // Example latitude
        val longitude = -73.99004//location.longitude // Example longitude

        // Call the searchPlaces method to initiate the search

        CoroutineScope(Dispatchers.IO).launch{
            val restaurants:RestaurantList = PlaceFinder().searchPlaces(keyword, radius, latitude, longitude)

            withContext(Dispatchers.Main){
                onPlacesReady(restaurants)
            }
        }
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
//        viewModel = ViewModelProvider(this).get(PlaceFinder::class.java)
//
//
//        viewModel.searchPlaces("", 100, 40.713713, 73.990041)

        val food = "pasta"
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


