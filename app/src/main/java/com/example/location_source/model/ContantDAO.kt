package com.example.location_source.model

import androidx.lifecycle.LiveData
import androidx.room.*



@Dao
interface ContantDAO {

    @Insert
    fun insertLocation(contact: AddLocationDataClass)

    @Update
     fun updateLocation(contact: AddLocationDataClass)

    @Delete
     fun deleteLocation(contact: AddLocationDataClass)


    @Query("SELECT * FROM location ORDER BY distance ASC")
     fun ascendingList() : LiveData<List<AddLocationDataClass>>


    @Query("SELECT * FROM location ORDER BY distance DESC")
     fun descendingList() : LiveData<List<AddLocationDataClass>>

}