package com.example.location_source.model

import androidx.lifecycle.LiveData

class Repository(private val dao: ContantDAO) {

    val ascLocationList: LiveData<List<AddLocationDataClass>> = dao.ascendingList()

    fun deleteLocation(location: AddLocationDataClass) {
        dao.deleteLocation(location)
    }

    fun insertLocation(location: AddLocationDataClass) {
        dao.insertLocation(location)
    }

    fun updateLocation(location: AddLocationDataClass) {
        dao.updateLocation(location)
    }
}

