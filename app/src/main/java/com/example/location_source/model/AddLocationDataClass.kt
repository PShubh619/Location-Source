package com.example.location_source.model

<<<<<<< HEAD
    data class AddLocationDataClass(
        val Location:String,
        val Address:String,
        val Distance:String,
        var Longitude: String,
        var Latitude: String,
        var IsPrimary:Boolean
        )
=======
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "location")
data class AddLocationDataClass(
    @PrimaryKey(autoGenerate = true)
    val primaryKey: Long,
    val location: String,
    val address: String?,
    val distance: String,
    var longitude: String,
    var latitude: String,
    var isPrimary: Boolean
)
>>>>>>> 4d3a1aa (DataBase)
