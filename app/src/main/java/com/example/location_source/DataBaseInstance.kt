package com.example.location_source

import android.app.Application
import androidx.room.Room
import com.example.location_source.model.AddLocationDataBase

class DataBaseInstance : Application() {
    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AddLocationDataBase::class.java, "database-name"
        ).build()
    }
}
