package com.example.lowkeytravelapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val name: String,
    val latitude: Double,
    val longitude: Double,
//    val price: String,
//    val rating: Float,
    val imageUrl:String,
//    val numReviews: Int,
//    val distance: Double,
//    val isOpen: Boolean,
//    val openingHours: String
// Optional, can be omitted if not needed
) : Parcelable

@Parcelize
data class RestaurantList(val restaurants: ArrayList<Restaurant>) : Parcelable
