package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidDatabaseDao
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NASAAPI
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val API_KEY = "mwIwNR2wBTnhoetdz3IQc9a8CymHZBCkfd4mnvC0"

class MainViewModel(private val asteroidDatabaseDao: AsteroidDatabaseDao) : ViewModel() {

    // Getting Response As List of Asteroids
    private var _AsteroidList = MutableLiveData<ArrayList<Asteroid>>()
    val AsteroidList : LiveData<ArrayList<Asteroid>>
        get() = _AsteroidList

    // Getting Image of the Day
    private var _imageOfTheDayURL = MutableLiveData<String>()
    val imageOfTheDayURL : LiveData<String>
        get() = _imageOfTheDayURL

    // Navigating To DetailFragment
    private var _navigateToDetailFragment = MutableLiveData<Asteroid>()
    val navigateToDetailFragment : LiveData<Asteroid>
        get() = _navigateToDetailFragment

    fun displayAsteroidDetails(asteroid: Asteroid){
        _navigateToDetailFragment.value = asteroid
    }
    fun doneNavigatingToDetailFragment(){
        _navigateToDetailFragment.value = null
    }

    // Getting Asteroids From Database
    var asteroidsFromDatabase = asteroidDatabaseDao.getAllAsteroids()

    // These Methods related to choosing from menu items
    fun getTodayAsteroidsOnWards(){
        asteroidsFromDatabase = asteroidDatabaseDao.getAsteroidsOfTodayOnWards(getTodayDataFormatted())
    }
    fun getAllSavedAsteroidsFromDatabase(){
        asteroidsFromDatabase = asteroidDatabaseDao.getAllAsteroids()
    }
    fun getNewWeeklyAsteroids(){
        getNearEarthElements(API_KEY)
        asteroidsFromDatabase = asteroidDatabaseDao.getAllAsteroids()
    }

    init {
        getNearEarthElements(API_KEY)
    }

    private fun getNearEarthElements(apiKey: String) {

        viewModelScope.launch {
            try{
                // Get Near EarthElements
                val JsonResponseString = NASAAPI.retrofitService.getAsteroidJson(apiKey)
                _AsteroidList.value = parseAsteroidsJsonResult(JSONObject(JsonResponseString))
                // Inserting Asteroids In AsteroidDatabase
                asteroidDatabaseDao.insertAsteroidsList(_AsteroidList.value!!)

            }catch (e: Exception){
                Log.i("FailureGettingElements:", e.message.toString())
            }

            try {
                // Get Image of the Day
                val imageOfTheDay = NASAAPI.retrofitService.getAsteroidImageOfTheDay(apiKey)
                _imageOfTheDayURL.value = imageOfTheDay.url
                Log.i("imageOfTheDayURL", imageOfTheDay.url)
            }catch (e: Exception){
                Log.i("FailureLoadingImage:", e.message.toString())
            }

        }
    }

    private fun getTodayDataFormatted(): String {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime).toString()
    }
}