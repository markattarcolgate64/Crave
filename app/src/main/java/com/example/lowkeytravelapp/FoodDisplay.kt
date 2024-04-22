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
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import org.json.JSONObject
import java.io.IOException

class FoodDisplayFragment : Fragment() {

    private lateinit var foodName: String
    private lateinit var foodImage: ImageView
    private lateinit var foodnameText: TextView
    private lateinit var lookupButton: Button
    private lateinit var nahbutton: Button
    private lateinit var yumbutton: Button
    var imageUrl = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fun fetchImageURL() {
            val apiKey = BuildConfig.GOOGLE_CLOUD_API_KEY
            val cx = "62bb355ee52724715"
            val query = foodName
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
                                Picasso.get().load(imageUrl)
                                    .transform(CropCircleTransformation())
                                    .into(foodImage)
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
        foodName = arguments?.getString("food").toString()
        // Initialize views
        foodImage = view.findViewById(R.id.food_image)
        foodnameText = view.findViewById(R.id.Food_text)
        lookupButton = view.findViewById(R.id.lookup_button)
        nahbutton = view.findViewById(R.id.Nah)
        yumbutton = view.findViewById(R.id.Yum)

        println("fetchImageURL()")

        foodnameText.text = "Yum! Try a: ${foodName}"
        fetchImageURL() //only 100 uses per day

        // Set food information text
        // Set click listener for the lookup button
        lookupButton.setOnClickListener {
            // For example:
            val wikipediaUrl = "https://en.wikipedia.org/wiki/$foodName" // Example Wikipedia URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
            startActivity(intent)
        }

        yumbutton.setOnClickListener {
            println("yum yum")
            (activity as? YumInterface)?.onYumButtonClick((foodnameText.text).toString())


         // put desired location into lat and long varibles
//            moves the camera---
//                latitude = 0.0
//                longitude = 0.0
//                (activity as? OnYumButtonClickListener)?.onYumButtonClick(latitude, longitude)

        }
        return view
    }

}
