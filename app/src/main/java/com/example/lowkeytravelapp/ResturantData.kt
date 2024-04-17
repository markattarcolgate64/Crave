package com.example.lowkeytravelapp

data class ResturantData(
    val name: String,
    val description: String,
    val price: Double,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageurl: String,
    val rating: Double,
    val open: Boolean
)