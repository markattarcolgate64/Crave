package com.example.lowkeytravelapp

interface OnPlacesReadyCallback {
    fun onPlacesReady(placesList: RestaurantList)
    fun onError(message: String)

}