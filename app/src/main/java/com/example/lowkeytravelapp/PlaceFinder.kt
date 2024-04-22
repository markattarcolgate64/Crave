package com.example.lowkeytravelapp

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class PlaceFinder{

        lateinit var callback: OnPlacesReadyCallback

        //It will need a list to hold all of the JSON entries probably
        lateinit var places: List<JSONObject>
        private var TAG = "PlacesActivity"
        private val NO_PLACES_FOUND = "ZERO_RESULTS"
        private var placesStore: ArrayList<Restaurant> = arrayListOf()




        //Method to conduct HTTPrequest to the Google places API
        fun searchPlaces(keyword:String, radius: Int, lat:Double, lon:Double){
            CoroutineScope(Dispatchers.IO).launch{
                val placesList = khttp.get(
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                            "&location=$lat%2C$lon" +
                            "&radius=$radius" +
                            "&keyword=$keyword" +
                            "&type=restaurant" +
                            "&key=${BuildConfig.GOOGLE_CLOUD_API_KEY}"
                )
                var placesjsonObject: JSONObject = placesList.jsonObject
                if (placesjsonObject.has("status")){
                    if (placesjsonObject.getString("status") == NO_PLACES_FOUND){
                        println("No places found")
                    } else {
                        try{
                            var next_place_token: String = ""

                            val resultsArr: JSONArray = placesjsonObject.getJSONArray("results")

                            parseResults(resultsArr, placesStore)

                            //While loop to parse the results repeatedly if there are subsequent pages past the first page
                            while (placesjsonObject.has("next_place_token")) {
                                next_place_token = placesjsonObject.getString("next_page_token")

                                //Make request to google places for next list
                                //Pull the results
                                val new_response = khttp.get(
                                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                                            "&pagetoken$next_place_token" +
                                            "&key=${BuildConfig.GOOGLE_CLOUD_API_KEY}"
                                )
                                placesjsonObject = new_response.jsonObject
                                //Enter any new into the results
                                val new_resultsArr: JSONArray = placesjsonObject.getJSONArray("results")
                                parseResults(new_resultsArr, placesStore)
                            }

                            callback.onPlacesReady(RestaurantList(placesStore))
                        } catch (error: Error){
                            Log.i(TAG, "DIDN'T WORK")
                        }
                    }
                }
            }
        }

        private fun parseResults(resultsArr:JSONArray, placesList: ArrayList<Restaurant>){
            for (i in 0..<resultsArr.length()){
                val placeObj: JSONObject = resultsArr.getJSONObject(i)
                if (placeObj.getString("business_status") == "OPERATIONAL"){
                    //Retrieving parameters for restaurant from the google places object
                    val placeName: String = placeObj.getString("name")
                    val location = placeObj.getJSONObject("geometry").getJSONObject("location")
                    val lat = location.getDouble("lat")
                    val lng = location.getDouble("lng")
                    val address = placeObj.getString("vicinity")

                    //Getting the photo and photo width for restaurant, first result in array
                    val photo = placeObj.getJSONArray("photos").getJSONObject(0)
                    val photoReference = photo.getString("photo_reference")
                    val width = photo.getInt("width")
                    val photoUrl = getImageUrl(photoReference, width)
                    placesList.add(Restaurant(placeName, lat, lng, address, photoUrl))
                }
            }

        }

        private fun sendResults(context: Context, placesList: ArrayList<Restaurant>, sendActivity: Class<out AppCompatActivity>){
            //Uses custom RestaurantList class that is a list of the Restaurant class
            val restaurants = RestaurantList(placesList)
            //Define the intent
            val sendIntent = Intent(context, sendActivity).putExtra("restaurantsList", restaurants)
            //Start the target activity
            context.startActivity(sendIntent)
        }

        private fun getImageUrl(photoReference: String, maxWidth: Int): String {
            val apiKey = BuildConfig.GOOGLE_CLOUD_API_KEY
            val baseUrl = "https://maps.googleapis.com/maps/api/place/photo"
            return "$baseUrl?photoreference=$photoReference&maxwidth=$maxWidth&key=$apiKey"
        }
    }




