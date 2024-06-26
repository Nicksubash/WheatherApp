package com.example.shockapp.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val baseUrl="https://api.weatherapi.com"
    val weatherAPI :WeatherAPI= getInstance().create(WeatherAPI::class.java)

    private fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}