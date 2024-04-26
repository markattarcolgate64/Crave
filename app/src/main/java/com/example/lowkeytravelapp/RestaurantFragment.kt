package com.example.lowkeytravelapp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
//import androidx.core.content.ContextCompat
//import kotlinx.android.synthetic.main.fragment_restaurant_card.*

class RestaurantFragment : Fragment() {

    private val dummyRestaurantList = ArrayList<Restaurant>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access fragmentRootLayout using findViewById
        val fragmentRootLayout = view.findViewById<View>(R.id.fragmentRootLayout)

        // Customize image and text content here
        // Example:
        // restaurantPhotoImageView.setImageResource(R.drawable.my_restaurant_photo)
        // restaurantNameTextView.text = "Restaurant Name"

        // Set random background color
        setRandomBackgroundColor(fragmentRootLayout)
    }

    private fun setRandomBackgroundColor(rootLayout: View) {
        val pastelColors = resources.getIntArray(R.array.pastel_colors)
        val randomIndex = pastelColors.indices.random()
        val randomColor = pastelColors[randomIndex]
        // Set background color of the fragment's root layout
        rootLayout.setBackgroundColor(randomColor)
    }
}
