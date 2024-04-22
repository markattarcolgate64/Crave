package com.example.lowkeytravelapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity(), FoodDisplayFragment.OnYumButtonClickListener {

    private val placeFinder = PlaceFinder()
    override fun onYumButtonClick(query: String) {
        // Call a method in MapsFragment to perform the location lookup
        val mapsFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as MapsFragment
        mapsFragment.searchPlaces(query)
        closeFoodDisplayFragment()

        // Example usage:
        val keyword = "restaurant"
        val radius = 5000 // Radius in meters
        val latitude = 37.7749 // Example latitude
        val longitude = -122.4194 // Example longitude

        // Call the searchPlaces method to initiate the search
        placeFinder.searchPlaces(this, MainActivity::class.java, keyword, radius, latitude, longitude)



    }

    private lateinit var overlayView: View // Semi-transparent overlay view

    override fun onCreate(savedInstanceState: Bundle?) {
        println("MAIN ACTIVITY")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        //Loads in main maps fragment
        //The restaurant data is passed to the maps fragment
        val restaurants = intent.getParcelableExtra("restaurantsList", RestaurantList::class.java )
        val mapFrag = MapsFragment()
        val lastKnownLocation = mapFrag.getLastKnownLocation()

        val mapArgs = Bundle()
        mapArgs.putParcelable("restaurantsList", restaurants)
        mapFrag.arguments = mapArgs

        replaceFragment(R.id.fragment_container, mapFrag)

        // Initialize the overlay view
        overlayView = findViewById(R.id.overlay)

        val latitude = 0.0
        val longitude = 0.0

        // Start with the overlay view hidden
        overlayView.visibility = View.GONE

        val findFoodButton = findViewById<Button>(R.id.find_food_button)

        // Set click listener for the button
        findFoodButton.setOnClickListener {
            replaceFragment(R.id.second_fragment_container, FoodDisplayFragment())
            fadeOutFragment(R.id.fragment_container) // Fade out the first fragment
        }

        // Set click listener for the overlay view to close FoodDisplayFragment
        overlayView.setOnClickListener {
            closeFoodDisplayFragment()
        }
    }

    private fun replaceFragment(containerId: Int, replacement: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, replacement)
            .commit()
    }

    private fun fadeOutFragment(containerId: Int) {
        // Fade out animation for the overlay view
        val fadeOutAnim = ObjectAnimator.ofFloat(overlayView, "alpha", 0f, 0.5f)
        fadeOutAnim.duration = 500 // Adjust duration as needed
        fadeOutAnim.start()

        // Show the overlay view
        overlayView.visibility = View.VISIBLE
    }

    private fun closeFoodDisplayFragment() {
        // Remove the FoodDisplayFragment from the second fragment container
        val fragment = supportFragmentManager.findFragmentById(R.id.second_fragment_container)
        fragment?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }

        // Fade in the original fragment
        val fadeInAnim = ObjectAnimator.ofFloat(overlayView, "alpha", 0.5f, 0f)
        fadeInAnim.duration = 500 // Adjust duration as needed
        fadeInAnim.start()

        // Hide the overlay view
        overlayView.visibility = View.GONE
    }
}


