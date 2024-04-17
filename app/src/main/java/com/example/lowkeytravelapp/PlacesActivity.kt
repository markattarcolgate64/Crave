package com.example.lowkeytravelapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class PlacesActivity: ComponentActivity() {
    private var TAG = "PlacesActivity"
    lateinit var testButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.places_choice)
        Log.i(TAG, "I think we're okay")

        testButton = findViewById(R.id.testButton)

        testButton.setOnClickListener{
            onTestClick()
        }
    }

    private fun onTestClick(){
        val placesManager = PlacesTester()
        placesManager.searchPlaces()


    }

    class PlacesTester{

        //It will need a list to hold all of the JSON entries probably
        lateinit var places: List<JSONObject>
        private var TAG = "PlacesActivity"
        private var placesStore: ArrayList<String> = arrayListOf()



        //Method to conduct HTTPrequest to the Google places API
        fun searchPlaces(){
            CoroutineScope(Dispatchers.IO).launch{
                val placesList = khttp.get(
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                            "&location=40.735674%2C-73.987484" +
                            "&radius=50" +
                            "&keyword=lasagna"+
                            "&type=restaurant" +
                            "&key=${BuildConfig.GOOGLE_CLOUD_API_KEY}"
                )


                    val placesjsonObject: JSONObject = placesList.jsonObject



                try{

                    var placesjsonObject: JSONObject = placesList.jsonObject
                    var next_place_token: String = ""
                    if (placesjsonObject.has("next_place_token")) {
                        next_place_token = placesjsonObject.getString("next_page_token")
                    }


                    val resultsArr: JSONArray = placesjsonObject.getJSONArray("results")



                    for (i in 0..<resultsArr.length()){
                        val placeName: String = resultsArr.getJSONObject(i).getString("name")
                        placesStore.add(placeName)
                    }

                    Log.i(TAG, "${placesList.jsonObject}")

                    val photosArray = placesList.jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("photos")
                    val photoReference = photosArray.getJSONObject(0).getString("photo_reference")
                    val maxWidth = photosArray.getJSONObject(0).getInt("width")
                    // Get image URL
                    val imageUrl = getImageUrl(photoReference, maxWidth)

                    Log.i("image",imageUrl)
//                    Log.i(TAG, "${placesList.jsonObject}")
//                    Log.i(TAG, "${placesList.jsonArray}")
                } catch (error: Error){
                    Log.i(TAG, "DIDN'T WORK")
                }
            }
        }
        private fun getImageUrl(photoReference: String, maxWidth: Int): String {
            val apiKey = BuildConfig.GOOGLE_CLOUD_API_KEY
            val baseUrl = "https://maps.googleapis.com/maps/api/place/photo"
            return "$baseUrl?photoreference=$photoReference&maxwidth=$maxWidth&key=$apiKey"
        }
    }
}



