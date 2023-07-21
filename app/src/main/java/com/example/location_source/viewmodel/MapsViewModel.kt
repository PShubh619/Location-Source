package com.example.location_source.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.location_source.DataBaseInstance
import com.example.location_source.model.AddLocationDataBase
import com.example.location_source.model.AddLocationDataClass
import com.example.location_source.model.ContantDAO
import com.example.location_source.model.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AddLocationDataBase = (application as DataBaseInstance).db
    private val dao: ContantDAO = db.dao()
    private val repository: Repository = Repository(dao)

    fun insertLocation(
        location: String,
        address: String?,
        distance: String,
        longitude: String,
        latitude: String,
        isPrimary: Boolean,
        lat: String,
        lng: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newLocation = AddLocationDataClass(
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
            repository.insertLocation(newLocation)
        }
    }
}

