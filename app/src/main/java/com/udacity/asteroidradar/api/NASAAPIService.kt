package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.AsteroidImageOfTheDay
import com.udacity.asteroidradar.Constants
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

// Using Moshi to get the json result of the image of the day as object
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


// Using Scalar converter and passing the Json response to get the Asteroids objects
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

interface NASAAPIService {
    // To get the List of Asteroids
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidJson(@Query("api_key") apiKey: String) : String

    @GET("planetary/apod")
    suspend fun getAsteroidImageOfTheDay(@Query("api_key") apiKey: String) : AsteroidImageOfTheDay

}

// Making Object to have only one instance of Retrofit
object NASAAPI {
    val retrofitService : NASAAPIService by lazy {
        retrofit.create(NASAAPIService::class.java)
    }
}
