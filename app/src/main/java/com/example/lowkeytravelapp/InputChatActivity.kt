package com.example.lowkeytravelapp

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import android.content.Intent
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.slider.Slider

class InputChatActivity: ComponentActivity() {
    private lateinit var output: String
    private lateinit var exploreButton: Button

    private lateinit var slider1: Slider
    private lateinit var slider2: Slider
    private lateinit var slider3: Slider
    private lateinit var slider4: Slider
    private lateinit var slider5: Slider
    private lateinit var slider6: Slider





    companion object{
        val TAG = "InputChatActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG,"On create")
        setContentView(R.layout.chat_input_screen)
//        chatInput = findViewById(R.id.chatEntry)
        exploreButton = findViewById(R.id.exploreButton)
        slider1 = findViewById(R.id.slider1)
        slider2 = findViewById(R.id.slider2)
        slider3 = findViewById(R.id.slider3)
        slider4 = findViewById(R.id.slider4)
        slider5 = findViewById(R.id.slider5)
        slider6 = findViewById(R.id.slider6)


//        radiusTextView = findViewById(R.id.radiusText)
//        val skip = 10
//        val radiusArr = Array(10) {(it * skip).toString()}
//        for (i in radiusArr){
//            Log.i(TAG,"${i}")
//        }

//        val radiusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, radiusArr)
//
//        radiusTextView.setAdapter(radiusAdapter)
//        radiusTextView.threshold=1
//
//        Log.i(TAG,"${radiusArr.toString()}")
         exploreButton.setOnClickListener{
             val intent = Intent(this, SliderFilter::class.java)
             startActivity(intent)
            }


    }




    fun exploreButton(){

//
//        var prompt: String = chatInput.text.toString()
//        val radius = radiusTextView.text

//        Log.i(TAG, "PROMPT: ${prompt}  RADIUS: ${radius}")

        //There are probably a couple of services we need to launch here
        //First would be packaging the data the correct way and then sending
        //It to Gemma, the next would be starting places

    }



    //What needs to happen in this activity is that the text needs to be sent to the LLM
    //I also need a field for radius and location
    //How should my radius button look?
    //Q: Rounded buttons, filters, type of place







}