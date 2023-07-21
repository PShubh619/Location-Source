package com.example.location_source.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng


@Entity(tableName = "location")
data class AddLocationDataClass(
    @PrimaryKey(autoGenerate = true)
    val primaryKey: Long,
    val location: String,
    val address: String?,
    val distance: String,
    var longitude: String,
    var latitude: String,
    var isPrimary: Boolean,
    var lat : String,
    var lng : String,
)