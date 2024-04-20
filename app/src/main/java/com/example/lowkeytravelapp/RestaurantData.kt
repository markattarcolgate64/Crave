package com.example.lowkeytravelapp

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val imageUrl:String
// Optional, can be omitted if not needed
) : Parcelable

@Parcelize
data class RestaurantList(val restaurants: ArrayList<Restaurant>) : Parcelable
