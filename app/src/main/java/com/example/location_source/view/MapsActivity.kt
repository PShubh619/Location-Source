package com.example.location_source.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.location_source.databinding.ActivityMapsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import android.view.View
import android.widget.Button

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.location_source.R
import com.example.location_source.model.AddLocationDataClass
import com.example.location_source.viewmodel.AddLocationViewModel
import com.example.location_source.viewmodel.MapsViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.SphericalUtil
import java.lang.String.format
import kotlin.math.log

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var clPermissionSave: ConstraintLayout
    private lateinit var toolbar: Toolbar
    private lateinit var locationEditText: AutocompleteSupportFragment
    private lateinit var btnSave: Button
    private var lastMarker: Marker? = null
    private var marker: Marker? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var arrayList: ArrayList<AddLocationDataClass>

    private var distanceInMeters: Float = 0f
    private var distanceInKM: Float = 0f
    private var currentAddress: String = ""
    private lateinit var cityName: String
    private var Latitude: String = ""
    private var Longitude: String = ""
    private var isFirstItem: Boolean = true
    private var last: LatLng = LatLng(0.0, 0.0)
    private var lastCheck: LatLng = LatLng(0.0, 0.0)
    private var lat:String=""
    private var lng:String=""

    private lateinit var viewModel: MapsViewModel
    private lateinit var listViewModel: AddLocationViewModel
  //  private var lastLatLng: LatLng? = null

    @SuppressLint("WrongViewCast", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        clPermissionSave = binding.clPermissionSave

        arrayList = ArrayList()

        toolbar = binding.toolBar
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val listJson = sharedPreferences.getString("List", "")
        if (!listJson.isNullOrEmpty()) {
            val gson = Gson()
            arrayList = gson.fromJson(
                listJson,
                object : TypeToken<ArrayList<AddLocationDataClass>>() {}.type
            )
            if (arrayList.isNotEmpty()) {
                isFirstItem = false
            }
        } else {
            arrayList = ArrayList()
        }

        listViewModel = ViewModelProvider(this).get(AddLocationViewModel::class.java)
        listViewModel.ascLocationList.observe(this) { locationList ->
          //      Log.d ("List:", locationList.toString())
            if (locationList.isNotEmpty()) {
                val lastLocation = locationList.last()
                last = LatLng(lastLocation.lat.toDouble(), lastLocation.lng.toDouble())
                lat = lastLocation.lat
                lng = lastLocation.lng

//                Latitude = last.latitude.toString() ?: ""
//                Longitude = last.longitude.toString() ?: ""
            }
//            else {
//                lastLatLng?.let {
//                    Latitude = it.latitude.toString()
//                    Longitude = it.longitude.toString()
//                }
//            }
        }

        Places.initialize(applicationContext, getString(R.string.google_map_api_key))
        locationEditText =
            supportFragmentManager.findFragmentById(R.id.fragSearch) as AutocompleteSupportFragment
        locationEditText.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        btnSave = binding.btnSave

        viewModel = ViewModelProvider(this).get(MapsViewModel::class.java)

        locationEditText.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {}

            @SuppressLint("DefaultLocale")
            override fun onPlaceSelected(p0: Place) {
                clPermissionSave.visibility = View.VISIBLE
                val locationName = locationEditText.toString()

                val addressList = p0.address
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
                    val latLng = p0.latLng

                    marker = mMap.addMarker(MarkerOptions().position(latLng).title(addressList))
                    marker?.showInfoWindow()
                    Latitude=marker?.position?.latitude.toString()
                    Longitude=marker?.position?.longitude.toString()

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))

                    if (last==lastCheck){
                    last = LatLng(Latitude.toDouble(), Longitude.toDouble())
                        lat = Latitude
                        lng = Longitude

                    }

                    marker?.let {
                        currentAddress = getAddressFromLatLng(marker!!.position)
                        cityName = getCityNameFromLatLng(marker!!.position)
                            distanceInMeters = calculateDistance(last, marker!!.position)
                            distanceInKM = distanceInMeters / 1000
                            distanceInKM = format("%.2f", distanceInKM).toFloat()
                    }
                    lastMarker = marker
                }
            }
        })

//        if (isFirstItem) {
//            lat = Latitude
//            lng = Longitude
//            isFirstItem = false
//        }

        btnSave.setOnClickListener {
            Log.d("distance", distanceInKM.toString())
            viewModel.insertLocation(
                cityName,
                currentAddress,
                distanceInKM.toString(),
                Longitude,
                Latitude,
                isFirstItem,
                lat,
                lng
            )
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val india = LatLng(22.0, 77.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 5f))

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark))
        } else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_default))
        }
    }

    private fun calculateDistance(latLng1: LatLng, latLng2: LatLng): Float {
        val distance = SphericalUtil.computeDistanceBetween(latLng1, latLng2)
        return distance.toFloat()
    }

    private fun getAddressFromLatLng(latLng: LatLng): String {
        val geocoder = Geocoder(this)
        val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return if (!addressList.isNullOrEmpty()) {
            addressList[0].getAddressLine(0)
        } else {
            ""
        }
    }

    private fun getCityNameFromLatLng(latLng: LatLng): String {
        val geocoder = Geocoder(this)
        val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return if (!addressList.isNullOrEmpty()) {
            addressList[0].locality ?: ""
        } else {
            ""
        }
    }
}

