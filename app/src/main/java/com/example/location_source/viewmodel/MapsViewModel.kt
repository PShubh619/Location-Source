package com.example.location_source.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.location_source.DataBaseInstance
import com.example.location_source.model.AddLocationDataClass
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    private  val db = (application as DataBaseInstance).db

    fun insertLocation(
        location: String,
        address: String?,
        distance: String,
        longitude: String,
        latitude: String,
        isPrimary: Boolean,
        lat: String,
        lng:String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            db.dao().insertLocation(
                AddLocationDataClass(
                    0,
                    location,
                    address,
                    distance,
                    longitude,
                    latitude,
                    isPrimary,
                    lat,
                    lng
                )
            )
        }
    }
}
