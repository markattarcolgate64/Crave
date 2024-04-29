package com.example.lowkeytravelapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.api.model.Place

class LocationSearch : AppCompatActivity() {

    private var useCurrentLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_search)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GOOGLE_CLOUD_API_KEY)
        }
        setupPlaceAutocomplete()
        setupCurrentLocationButton()
    }

    private fun setupPlaceAutocomplete() {
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let {
                    startInputChatActivity(it.latitude, it.longitude, useCurrentLocation)
                }
            }
            override fun onError(status: com.google.android.gms.common.api.Status) {
                // Handle the error.
                Log.i(TAG, "Error processing autocomplete")
            }
        })
    }

    private fun setupCurrentLocationButton() {
        val useLocation = findViewById<Button>(R.id.button_current_location)

        useLocation.setOnClickListener {
            useCurrentLocation = true
            startInputChatActivity(0.0,0.0, useCurrentLocation)
        }
    }

    private fun startInputChatActivity(latitude: Double, longitude: Double, useCurrent: Boolean) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            putExtra("useCurrent", useCurrent)
        }
        startActivity(intent)
    }

    companion object {
        const val TAG = "LocationSearch"
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    }
}
