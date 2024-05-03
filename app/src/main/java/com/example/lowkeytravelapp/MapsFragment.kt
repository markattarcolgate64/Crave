package com.example.lowkeytravelapp

//import com.akexorcist.googledirection.sample.databinding.ActivitySimpleDirectionBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class MapsFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val TAG = "MapsFragment"
        const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    }


    var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private var polyline: Polyline? = null


    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var apikey = BuildConfig.GOOGLE_CLOUD_API_KEY
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    private var origin = defaultLocation
    private var destination = LatLng(37.7814432, -122.4460177)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mapView = inflater.inflate(R.layout.fragment_maps, container, false)
        println("GOOGLE MAPS")

        return mapView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Places SDK
        Places.initialize(requireContext(), apikey)
        Places.initialize(requireContext(), BuildConfig.GOOGLE_CLOUD_API_KEY)
        placesClient = Places.createClient(requireContext())

        println("maps fragment")

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLocationPermission()
        lastKnownLocation?.let { moveCamera(it.latitude, lastKnownLocation!!.longitude) }
        updateLocationUI()
        getDeviceLocation()

        println("arguments $arguments")

        val restaurantList = arguments?.getParcelable("restaurantsList", RestaurantList::class.java)
        val restaurants: ArrayList<Restaurant> = restaurantList!!.restaurants
        for (restaurant in restaurants) {
            val location = LatLng(restaurant.latitude, restaurant.longitude)
            println("location: $location Name: ${restaurant.name}")
            map!!.addMarker(MarkerOptions().position(location).title(restaurant.name))
        }

        requireActivity().supportFragmentManager.setFragmentResultListener("restaurantClick", requireActivity()) { requestKey, Bundle ->
            val clickRestaurant = Bundle.getParcelable("restaurant", Restaurant::class.java)
            moveCamera(clickRestaurant!!.latitude, clickRestaurant.longitude)
            destination = LatLng(clickRestaurant.latitude, clickRestaurant.longitude)
            getDeviceLocation()
            requestDirection()
        }

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        lastKnownLocation = location
                        origin = LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)
                        moveCamera(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        moveCamera(defaultLocation.latitude,defaultLocation.longitude)
                        origin = defaultLocation
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

//    fun searchPlaces(query: String) {
//        val request = FindAutocompletePredictionsRequest.builder()
//            .setQuery(query)
//            .build()
//
//        placesClient.findAutocompletePredictions(request)
//            .addOnSuccessListener { response ->
//                for (prediction in response.autocompletePredictions) {
//                    println("Predictions")
//                    Log.i(TAG, prediction.placeId)
//                    Log.i(TAG, prediction.getPrimaryText(null).toString())
//                    // You can add markers or handle search results here
//                }
//            }
//            .addOnFailureListener { exception ->
//                if (exception is ApiException) {
//                    Log.e(TAG, "Place not found: ${exception.statusCode}")
//                }
//            }
//    }

    fun getLastKnownLocation(): Location? {
        return lastKnownLocation
    }

    private fun moveCamera(latitude: Double, longitude: Double) {
        //Implement code to move camera
        Log.i(TAG, "Interface working")
        map?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), DEFAULT_ZOOM.toFloat()
            )
        )
    }

    private fun requestDirection() {
        // Handle request direction button click
        println(getString(R.string.direction_requesting))

        // Set up Google Direction API configuration
        GoogleDirectionConfiguration.getInstance().isLogEnabled = BuildConfig.DEBUG
        lastKnownLocation?.let { LatLng(it.latitude, lastKnownLocation!!.longitude) }?.let {
            GoogleDirection.withServerKey(BuildConfig.GOOGLE_CLOUD_API_KEY)
                .from(it)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(
                    onDirectionSuccess = { direction -> onDirectionSuccess(direction) },
                    onDirectionFailure = { t -> onDirectionFailure(t) }
                )
        }
    }

    private fun onDirectionSuccess(direction: Direction?) {
        // Handle direction request success
        direction?.let {
            println(getString(R.string.success_with_status, direction.status))
            if (direction.isOK) {
                val route = direction.routeList[0]
//                map?.addMarker(MarkerOptions().position(origin))
//                map?.addMarker(MarkerOptions().position(destination))
                val directionPositionList = route.legList[0].directionPoint
                polyline?.remove()
                polyline=map?.addPolyline(
                    DirectionConverter.createPolyline(
                        requireContext(),
                        directionPositionList,
                        5,
                        Color.BLUE
//                        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    )
                )
                setCameraWithCoordinationBounds(route)
            } else {
                println(direction.status)
            }
        } ?: run {
            println(getString(R.string.success_with_empty))
        }
    }

    private fun onDirectionFailure(t: Throwable) {
        // Handle direction request failure
        println(t.message)
    }

    private fun setCameraWithCoordinationBounds(route: Route) {
        // Set camera view with coordination bounds
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }


}