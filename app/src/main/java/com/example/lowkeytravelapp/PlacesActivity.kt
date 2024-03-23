package com.example.lowkeytravelapp

import org.json.JSONObject
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    fun onTestClick(){
        val placesManager = PlacesTester()
        placesManager.searchPlaces()

    }

    class PlacesTester{

        //It will need a list to hold all of the JSON entries probably
        lateinit var places: List<JSONObject>
        private var TAG = "PlacesActivity"


        //Method to conduct HTTPrequest to the Google places API
        fun searchPlaces(){
            CoroutineScope(Dispatchers.IO).launch{
                val placesList = khttp.get(
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?&location=40.735674%2C-73.987484&radius=1000&key=${BuildConfig.GOOGLE_CLOUD_API_KEY}"

                )
                try{

                    var placesjsonObject: JSONObject = placesList.jsonObject

                    placesjsonObject.get("NextPageToken")


                    Log.i(TAG, "${placesList.jsonObject}")
 //                   Log.i(TAG, "${placesList.jsonObject}")
//                  Log.i(TAG, "${placesList.jsonArray}")
                } catch (error: Error){
                    Log.i(TAG, "DIDN'T WORK")
                }


            }
        }

    }

}



