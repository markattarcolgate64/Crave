package com.example.lowkeytravelapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lowkeytravelapp.R.layout
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

class FoodDisplayFragment : Fragment() {

    private lateinit var foodName: String
    private lateinit var foodImage: ImageView
    private lateinit var foodInfoText: TextView
    private lateinit var foodnameText: TextView
    private lateinit var lookupButton: Button
    private lateinit var nahbutton: Button
    private lateinit var yumbutton: Button
    var imageUrl = ""

    private lateinit var overlayView: View // Semi-transparent overlay view


    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fun fetchImageURL() {
            val apiKey = BuildConfig.IMAGE_FETCH_API_KEY
            val cx = "62bb355ee52724715"
            val query = "Sandwich"
            val url = "https://www.googleapis.com/customsearch/v1?key=$apiKey&cx=$cx&q=$query&searchType=image&google_domain=com&gl=us&hl=en"

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(response: Response?) {
                    val responseBodyString = response?.body()?.string()
                    if (!responseBodyString.isNullOrEmpty()) {
                        val json = JSONObject(responseBodyString)
                        if (json.has("items")) {
                            val items = json.getJSONArray("items")
                            val firstItem = items.getJSONObject(0)
                            imageUrl = firstItem.getString("link")
                            println(imageUrl)
                            activity?.runOnUiThread {
                                Picasso.get().load(imageUrl).into(foodImage)
                            }
                        } else {
                            println("No items found in the response.")
                        }
                    } else {
                        println("Empty response body.")
                    }
                }
                override fun onFailure(request: Request?, e: IOException?) {
                    e?.printStackTrace()
                }
            })
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(layout.fragment_food_display, container, false)

        // Initialize views
        foodImage = view.findViewById(R.id.food_image)
        foodInfoText = view.findViewById(R.id.food_information_text)
        foodnameText = view.findViewById(R.id.Food_text)
        lookupButton = view.findViewById(R.id.lookup_button)
        nahbutton = view.findViewById(R.id.Nah)
        yumbutton = view.findViewById(R.id.Yum)

        println("fetchImageURL()")
        fetchImageURL() //only 100 uses per day

        // Set food information text
        val foodInfo = "Food information will be displayed here..." // Replace with actual food information
        foodInfoText.text = foodInfo

        // Set click listener for the lookup button
        lookupButton.setOnClickListener {
            // For example:
            foodName = "Sandwich" // Replace with actual food name
            val wikipediaUrl = "https://en.wikipedia.org/wiki/$foodName" // Example Wikipedia URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
            startActivity(intent)
        }

        yumbutton.setOnClickListener {
            println("yum yum")
            (activity as? OnYumButtonClickListener)?.onYumButtonClick((foodnameText.text).toString())


         // put desired location into lat and long varibles
//            moves the camera---
//                latitude = 0.0
//                longitude = 0.0
//                (activity as? OnYumButtonClickListener)?.onYumButtonClick(latitude, longitude)

        }
        return view
    }
    interface OnYumButtonClickListener {
        fun onYumButtonClick(query: String)
    }
}
