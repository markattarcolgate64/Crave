package com.example.lowkeytravelapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


class MainActivity : AppCompatActivity(), OnPlacesReadyCallback,
    FoodDisplayStackFragment.OnFragmentClosedListener {
    private var lastKnownLocation: Location? = null
    private var latitude = 40.713713//location!!.latitude
    // Example latitude
    private var longitude = -73.99004//location.longitude // Example longitude
    private val defaultLocation = LatLng(40.713713, -73.99004)
    private var locationPermissionGranted = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object{
        val TAG = "MainActivity"

    }

    override fun onPlacesReady(placesList: RestaurantList) {
        val mapFrag = MapsFragment()
        val mapArgs = Bundle()
        mapArgs.putParcelable("restaurantsList", placesList)
        mapArgs.putDouble("latitude", latitude)
        mapArgs.putDouble("longitude", longitude)
        mapFrag.arguments = mapArgs
        replaceFragment(R.id.fragment_container, mapFrag)

        val restaurantFragmentScroll = RestaurantFragmentScroll()
        val scrollArgs = Bundle()
        scrollArgs.putParcelable("restaurantsList", placesList)
        restaurantFragmentScroll.arguments = scrollArgs
        replaceFragment(R.id.restaurant_fragment_container,restaurantFragmentScroll)
    }

    override fun onError(message: String) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        println("MAIN ACTIVITY")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        gptFilter()
        //Loads in FoodDisplay fragment
        //The restaurant data is passed to the maps fragment
        //viewModel = ViewModelProvider(this).get(PlaceFinder::class.java)
        //37.422131, -122.084801
        //viewModel.searchPlaces("", 100, 40.713713, 73.990041)
        //Loads in FoodDisplay fragment
        //The restaurant data is passed to the maps fragment
//        viewModel = ViewModelProvider(this).get(PlaceFinder::class.java)
//
//
//        viewModel.searchPlaces("", 100, 40.713713, 73.990041)

    }

    private fun gptFilter(){

        val sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
        val light = sharedPreferences.getFloat("light", 0.0F)
        val sweet = sharedPreferences.getFloat("sweet", 0.0F)
        val mild = sharedPreferences.getFloat("mild", 0.0F)
        val west = sharedPreferences.getFloat("west", 0.0F)
        val veggie = sharedPreferences.getFloat("veggie", 0.0F)
        val time = sharedPreferences.getFloat("time", 0.0F)

        Log.d("Slider Debug: ", light.toString())
        Log.d("Slider Debug: ", sweet.toString())
        Log.d("Slider Debug: ", mild.toString())
        Log.d("Slider Debug: ", west.toString())
        Log.d("Slider Debug: ", veggie.toString())
        Log.d("Slider Debug: ", time.toString())

        var light_value = ""
        var sweet_value = ""
        var mild_value = ""
        var west_value = ""
        var veggie_value = ""
        var time_value = ""

        light_value = if ( light < 1 ){
            "Very Light to Eat"
        }else if(light < 2 ){
            "Relatively Light to Eat"
        }else if(light < 3 ){
            "Not too heavy, not to light to eat. But slightly on the light side"
        }else if(light < 4 ){
            "Not too heavy, not to light to eat. But slightly on the heavy side"
        }else if(light < 5 ){
            "Relatively heavy to Eat"
        }else{
            "Very heavy to Eat"
        }

        sweet_value = if ( sweet < 1 ){
            "Very sweet"
        }else if(sweet < 2 ){
            "Relatively sweet"
        }else if(sweet < 3 ){
            "Not too sweet, not to savory to eat. But slightly on the sweet side"
        }else if(sweet < 4 ){
            "Not too sweet, not to savory to eat. But slightly on the savory side"
        }else if(sweet < 5 ){
            "Relatively savory"
        }else{
            "Very savory"
        }

        mild_value = if ( mild < 1 ){
            "Very mild. "
        }else if(mild < 2 ){
            "Relatively mild, slight amount of spice"
        }else if(mild < 3 ){
            "slightly below average spice level"
        }else if(mild < 4 ){
            "slightly above average spice level"
        }else if(mild < 5 ){
            "Relatively spicy"
        }else{
            "Very spicy"
        }

        west_value = if ( west < 1 ){
            "Comes From one of those cuisines: Mexican, Peruvian, Brazilian, Argentinian, Cuban, Jamaican, Cajun, Creole, American, Canadian. "
        }else if(west < 2 ){
            "Comes From one of those cuisines: Icelandic, Irish, British, French, Spanish, Portuguese, Italian, Swiss, Austrian, German. "
        }else if(west < 3 ){
            "Comes From one of those cuisines: Moroccan, Algerian, Tunisian, Libyan, Egyptian, Ethiopian, Kenyan, Tanzanian, South African"
        }else if(west < 4 ){
            "Comes From one of those cuisines: Turkish, Lebanese, Israeli, Russian, Georgian, Iranian, Afghan"
        }else if(west < 5 ){
            "Comes From one of those cuisines: Pakistani, Nepalese, Bhutanese, Bangladeshi, Indian, Thain, Vietnamese, Cambodian, Laotian"
        }else{
            "Comes From one of those cuisines:  Burmese, Malaysian, Singaporean, Indonesian, Filipino, Chinese, Korean, Japanese, Australian"
        }

        veggie_value = if ( veggie < 1 ){
            "Fully Vegetarian "
        }else if(veggie < 2 ){
            "Majorly Vegetarian but may contain some meat. "
        }else if(veggie < 3 ){
            "Combination of meat and veggies, but slightly heavier on veggies"
        }else if(veggie < 4 ){
            "Combination of meat and veggies, but slightly heavier on meat"
        }else if(veggie < 5 ){
            "Majorly meat dish but may contain some veggies."
        }else{
            "Made of only meat"
        }


        time_value = if ( time < 1 ){
            "Served as a breakfast "
        }else if(time < 2 ){
            "Served as a mead in between breakfast and lunch "
        }else if(time < 3 ){
            "served as a lunch"
        }else if(time < 4 ){
            "Meal in between lunch and dinner"
        }else if(time < 5 ){
            "Dinner"
        }else{
            "Late night snack/food"
        }

        val food_description = "Generate me a name of a 5 dishes separated by commas that has the following description:" + light_value + ", " + sweet_value + ", " + mild_value + ", " + west_value + ", " + veggie_value + ", " + time_value
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization",BuildConfig.GPT_API_KEY)
                    .method(original.method, original.body)
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        val apiService = retrofit.create(OpenAIApiService::class.java)

        val messages = listOf(Message("user", food_description))
        val requestBody = ChatRequestBody("gpt-4", messages)

        apiService.getChatResponse(requestBody).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    val food = chatResponse?.choices?.firstOrNull()?.message?.content
                    val foodString: List<String> = food.toString().split(", ")
                    val foodStackFrag = FoodDisplayStackFragment()
                    val foodArgs = Bundle()
                    val foodList = ArrayList(foodString)
                    foodArgs.putStringArrayList("food", foodList)
                    foodStackFrag.arguments = foodArgs
                    replaceFragment(R.id.fragment_container, foodStackFrag)
                    // Set this activity as the listener for fragment events
                    Log.d("Return from GPT","Response: ${chatResponse?.choices?.firstOrNull()?.message?.content}" )
                } else {
                    println("Failed to get response")
                    Log.d(TAG, "Failed")
                    val errorBody = response.errorBody()?.string()
                    Log.e("GPT ERROR OCCURRED WITH API", "Failed with status: ${response.code()} and error: $errorBody")
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }

    override fun onFragmentClosed(data: String) {
        // Handle the data received from the fragment here
        println("Data received from fragment: $data ")
        val radius = 500 // Radius in meters
        latitude = 40.713713//location!!.latitude
        longitude = -73.99004
        getLocationPermission()
        try {
            if (locationPermissionGranted) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        lastKnownLocation = location
                        latitude = lastKnownLocation!!.latitude
                        longitude = lastKnownLocation!!.longitude
                        Log.i(TAG, "GOT INSIDE LOCATION PROVIDER")
                        CoroutineScope(Dispatchers.IO).launch{
                            val restaurants:RestaurantList = PlaceFinder().searchPlaces(
                                data,
                                radius,
                                latitude,
                                longitude
                            )
                            withContext(Dispatchers.Main){
                                onPlacesReady(restaurants)
                            }
                        }
                    } else {
                        Log.d(MapsFragment.TAG, "Current location is null. Using defaults.")
                        //getLocationPermission()
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }

            //location.longitude // Call the searchPlaces method to initiate the search

    }

    private fun replaceFragment(containerId: Int, replacement: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, replacement)
            .commit()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}




interface OpenAIApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    fun getChatResponse(@Body requestBody: ChatRequestBody): Call<ChatResponse>
}

data class ChatRequestBody(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

data class ChatResponse(
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
