package com.example.lowkeytravelapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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


class MainActivity : AppCompatActivity(), YumInterface, OnPlacesReadyCallback {

    //private lateinit var viewModel: PlaceFinder

    companion object{
        val TAG = "MainActivity"
    }

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

        gptFilter()
        //Loads in FoodDisplay fragment
        //The restaurant data is passed to the maps fragment
        //viewModel = ViewModelProvider(this).get(PlaceFinder::class.java)
        //37.422131, -122.084801
        //viewModel.searchPlaces("", 100, 40.713713, 73.990041)
    }

    private fun replaceFragment(containerId: Int, replacement: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, replacement)
            .commit()
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





        if ( light < 1 ){
            light_value = "Very Light to Eat"
        }else if(light < 2 ){
            light_value = "Relatively Light to Eat"
        }else if(light < 3 ){
            light_value = "Not too heavy, not to light to eat. But slightly on the light side"
        }else if(light < 4 ){
            light_value = "Not too heavy, not to light to eat. But slightly on the heavy side"
        }else if(light < 5 ){
            light_value = "Relatively heavy to Eat"
        }else{
            light_value = "Very heavy to Eat"
        }


        if ( sweet < 1 ){
            sweet_value = "Very sweet"
        }else if(sweet < 2 ){
            sweet_value = "Relatively sweet"
        }else if(sweet < 3 ){
            sweet_value = "Not too sweet, not to savory to eat. But slightly on the sweet side"
        }else if(sweet < 4 ){
            sweet_value = "Not too sweet, not to savory to eat. But slightly on the savory side"
        }else if(sweet < 5 ){
            sweet_value = "Relatively savory"
        }else{
            sweet_value = "Very savory"
        }

        if ( mild < 1 ){
            mild_value = "Very mild. "
        }else if(mild < 2 ){
            mild_value = "Relatively mild, slight amount of spice"
        }else if(mild < 3 ){
            mild_value = "slightly below average spice level"
        }else if(mild < 4 ){
            mild_value = "slightly above average spice level"
        }else if(mild < 5 ){
            mild_value = "Relatively spicy"
        }else{
            mild_value = "Very spicy"
        }

        if ( west < 1 ){
            west_value = "Comes From Cuba. "
        }else if(west < 2 ){
            west_value = "Comes From Spain. "
        }else if(west < 3 ){
            west_value = "Comes from Egypt"
        }else if(west < 4 ){
            west_value = "Comes from India"
        }else if(west < 5 ){
            west_value = "Comes From Japan"
        }else{
            west_value = "Comes from Australia"
        }

        if ( veggie < 1 ){
            veggie_value = "Fully Vegetarian "
        }else if(veggie < 2 ){
            veggie_value = "Majorly Vegetarian but may contain some meat. "
        }else if(veggie < 3 ){
            veggie_value = "Combination of meat and veggies, but slightly heavier on veggies"
        }else if(veggie < 4 ){
            veggie_value = "Combination of meat and veggies, but slightly heavier on meat"
        }else if(veggie < 5 ){
            veggie_value = "Majorly meat dish but may contain some veggies."
        }else{
            veggie_value = "Made of only meat"
        }


        if ( time < 1 ){
            time_value = "Served as a breakfast "
        }else if(time < 2 ){
            time_value = "Served as a mead in between breakfast and lunch "
        }else if(time < 3 ){
            time_value = "served as a lunch"
        }else if(time < 4 ){
            time_value = "Meal in between lunch and dinner"
        }else if(time < 5 ){
            time_value = "Dinner"
        }else{
            time_value = "Late night snack/food"
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
                    val foodString = food.toString().split(", ")
                    val FoodFrag = FoodDisplayFragment()
                    val foodArgs = Bundle()
                    foodArgs.putString("food", foodString[0])
                    FoodFrag.arguments = foodArgs
                    replaceFragment(R.id.fragment_container, FoodFrag)
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
//

//        headerElement = findViewById(R.id.header)
//        val floatValue = intent.getFloatExtra("Key", 0.0f)


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






