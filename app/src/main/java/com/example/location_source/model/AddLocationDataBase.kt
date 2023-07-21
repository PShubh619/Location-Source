package com.example.location_source.model

import androidx.room.Database
import androidx.room.RoomDatabase



@Database(entities =  [AddLocationDataClass::class], version = 1)
abstract class AddLocationDataBase : RoomDatabase() {
    abstract fun dao(): ContantDAO
}