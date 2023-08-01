package com.example.location_source.view

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.location_source.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.location_source.databinding.ActivityMapsRoutingBinding
import com.example.location_source.viewmodel.AddLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MapsRoutingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsRoutingBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var OrdeOfList: String
//    private var Location: LatLng = LatLng(0.0, 0.0)
    var firstLocation: LatLng? = null
    private val coordinatesList: MutableList<LatLng> = mutableListOf()

    private lateinit var viewModel: AddLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps_routing)



        viewModel = ViewModelProvider(this).get(AddLocationViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        OrdeOfList = sharedPreferences.getString("OrderOfList", "").toString()

        if (OrdeOfList.isNullOrEmpty()){
            OrdeOfList="Ascending"
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        if (OrdeOfList == "Ascending"){
        viewModel.ascLocationList.observe(this) { locationList ->
            coordinatesList.clear() // Clear the list before populating it with new coordinates
            for (location in locationList) {
                val latitude = location.latitude.toDouble()
                val longitude = location.longitude.toDouble()
                val latLng = LatLng(latitude, longitude)
                coordinatesList.add(latLng)
            }
            drawRoute(coordinatesList)
        }
        }
        if (OrdeOfList == "Descending"){
        viewModel.descLocationList.observe(this) { locationList ->

            coordinatesList.clear() // Clear the list before populating it with new coordinates
            for (location in locationList) {
                Log.d("List", "$location" )
                val latitude = location.latitude.toDouble()
                val longitude = location.longitude.toDouble()
                val latLng = LatLng(latitude, longitude)
                coordinatesList.add(latLng)
            }
            drawRoute(coordinatesList)
        }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark))
        } else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_default))
        }
    }

    private fun drawRoute(locations: List<LatLng>) {
        for (location in locations) {
            val waypoint = LatLng(location.latitude, location.longitude)
            mMap?.addMarker(MarkerOptions().position(waypoint))
        }

        val waypoints = ArrayList(locations)

        val url = getDirectionURL(waypoints, getString(R.string.google_map_api_key))
        getDirection( mMap ,url, waypoints)
    }


    private fun getDirectionURL(waypoints: List<LatLng>, secret: String): String {
        val origin = waypoints.first()
        val destination = waypoints.last()
        val waypoint = StringBuilder()
        for (i in 1 until waypoints.size - 1) {
            waypoint.append("via%3A${waypoints[i].latitude}%2C${waypoints[i].longitude}%7C")
        }
        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "&origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&waypoints=${waypoint}" +
                "&key=$secret"
    }

     fun getDirection(mMap: GoogleMap?, url: String, waypoints: ArrayList<LatLng>) {
        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()


            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                val legs = respObj.routes[0].legs
                for (leg in legs) {
                    for (step in leg.steps) {
                        path.addAll(decodePolyline(step.polyline.points))
                    }
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                val lineOption = PolylineOptions()
                for (i in result.indices) {
                    lineOption.addAll(result[i])
                    lineOption.width(12f)
                    lineOption.color(Color.RED)
                    lineOption.geodesic(true)
                }
                for (i in waypoints.indices) {
                    val waypoint = waypoints[i]
                    mMap?.addMarker(MarkerOptions().position(waypoint))

//                    if (OrdeOfList == "Ascending") {
                        if (i == 0) {
                            firstLocation = waypoint
                        }
//                    }
//                    if (OrdeOfList == "Descending") {
//                        if (i == waypoints.lastIndex ) {
//                            firstLocation = waypoint
//                        }
//                    }
                }
                firstLocation?.let {
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 10f))
                }
//                val india = LatLng(22.0, 77.0)
//                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 5f))
                mMap?.addPolyline(lineOption)
            }
        }
    }


    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
}



//    private fun requestRoute(origin: LatLng, destination: LatLng) {
//        val apiService = DirectionsApi.getDirections()
//        val request = DirectionsApiRequest(apiService)
//
//        // Set the origin and destination
//        request.origin(origin)
//        request.destination(destination)
//        request.mode(TravelMode.DRIVING)
//
//        request.setCallback(object : PendingResult.Callback<DirectionsResult>,
//            com.google.maps.PendingResult.Callback<DirectionsResult> {
//            override fun onResult(result: DirectionsResult?) {
//                if (result?.routes?.isNotEmpty() == true) {
//                    val route = result.routes[0]
//                    val overviewPolyline = route.overviewPolyline
//                    val decodedPath = PolyUtil.decode(overviewPolyline.encodedPath)
//
//                    // Draw the route polyline on the map
//                    mMap.addPolyline(PolylineOptions().addAll(decodedPath))
//                }
//            }
//
//            override fun onFailure(e: Throwable) {
//                // Handle request failure
//            }
//        })
//    }

