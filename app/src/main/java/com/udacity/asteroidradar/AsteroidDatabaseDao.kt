package com.udacity.asteroidradar

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface AsteroidDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsteroidsList(list: ArrayList<Asteroid>)

    // Get Asteroids With Certain data

    @Query("SELECT * FROM asteroids_list_table WHERE closeApproachDate >= :todayDate ORDER BY closeApproachDate ASC")
    fun getAsteroidsOfTodayOnWards(todayDate: String) : LiveData<List<Asteroid>>

    // Get All Asteroids
    @Query("SELECT * FROM asteroids_list_table")
    fun getAllAsteroids() : LiveData<List<Asteroid>>


}