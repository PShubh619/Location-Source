package com.example.location_source.model

    data class AddLocationDataClass(
        val Location:String,
        val Address:String,
        val Distance:String,
        var Longitude: String,
        var Latitude: String,
        var IsPrimary:Boolean
        )
