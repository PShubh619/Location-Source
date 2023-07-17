package com.example.location_source.model

import androidx.lifecycle.LiveData
import androidx.room.*



@Dao
interface ContantDAO {

    @Insert
    suspend fun insertLocation(contact: AddLocationDataClass)

    @Update
    suspend fun updateLocation(contact: AddLocationDataClass)

    @Delete
    suspend fun deleteLocation(contact: AddLocationDataClass)


//    @Query("SELECT * FROM location ORDER BY distance ASC")
//    suspend fun ascendingList() : LiveData<List<AddLocationDataClass>>
//
//
//    @Query("SELECT * FROM location ORDER BY distance DESC")
//    suspend fun descendingList() : LiveData<List<AddLocationDataClass>>

}