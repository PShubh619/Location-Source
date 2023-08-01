package com.example.location_source.model

import androidx.lifecycle.LiveData

class Repository(private val dao: ContantDAO) {

    val ascLocationList: LiveData<List<AddLocationDataClass>> = dao.ascendingList()
    val descLocationList: LiveData<List<AddLocationDataClass>> = dao.descendingList()

    fun deleteLocation(location: AddLocationDataClass) {
        dao.deleteLocation(location)
    }

    fun insertLocation(location: AddLocationDataClass) {
        dao.insertLocation(location)
    }

}

