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
import android.content.Context
import android.content.SharedPreferences
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.slider.Slider

class SplashScreenActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.front_activity)

    }

}