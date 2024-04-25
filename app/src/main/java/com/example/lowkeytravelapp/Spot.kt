package com.example.lowkeytravelapp

data class Spot(
    val id: Long = counter++,
    val name: String,
    val url: String
) {
    companion object {
        private var counter = 0L
    }
}
