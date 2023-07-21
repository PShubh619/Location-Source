package com.example.location_source.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.location_source.DataBaseInstance
import com.example.location_source.model.AddLocationDataBase
import com.example.location_source.model.AddLocationDataClass
import com.example.location_source.model.ContantDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddLocationViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AddLocationDataBase = (application as DataBaseInstance).db
    private val dao: ContantDAO = db.dao()

    val ascLocationList: LiveData<List<AddLocationDataClass>> = dao.ascendingList()
    val descLocationList: LiveData<List<AddLocationDataClass>> = dao.descendingList()

//    fun insertLocation(location: AddLocationDataClass) {
//        viewModelScope.launch(Dispatchers.IO) {
//            dao.insertLocation(location)
//        }
//    }

    fun updateLocation(location: AddLocationDataClass) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateLocation(location)
        }
    }

    fun deleteLocation(location: AddLocationDataClass) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteLocation(location)
        }
    }
}
