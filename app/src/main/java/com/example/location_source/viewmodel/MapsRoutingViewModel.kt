package com.example.location_source.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.location_source.DataBaseInstance
import com.example.location_source.model.AddLocationDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsRoutingViewModel(application: Application) : AndroidViewModel(application) {
    private  val db = (application as DataBaseInstance).db
    private val _locationsLiveData = MutableLiveData<List<AddLocationDataClass>>()
    val locationsLiveData: MutableLiveData<List<AddLocationDataClass>> get() = _locationsLiveData

    fun getLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = db.dao().ascendingList().value
            if (locations == null) {
                _locationsLiveData.postValue(locations!!)
            }
        }
    }
}
