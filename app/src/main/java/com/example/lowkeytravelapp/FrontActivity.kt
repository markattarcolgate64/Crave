package com.example.lowkeytravelapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity

class FrontActivity: ComponentActivity(){
    private lateinit var nextButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.front_activity)

        nextButton = findViewById(R.id.nextButton)

        nextButton.setOnClickListener{


            val intent = Intent(this, InputChatActivity::class.java)
            startActivity(intent)
//             intent.putExtra("Slider1", slider1Value)

        }


    }
}