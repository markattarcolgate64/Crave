package com.example.lowkeytravelapp


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
//        setContentView(R.layout.splash)
//
//        Handler().postDelayed({
//            startActivity(Intent(this, InputChatActivity::class.java))
//            finish()
//        }, 3000) // 3000 is the delayed time in milliseconds.
    }
}