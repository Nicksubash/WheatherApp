package com.example.shockapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shockapp.API.Constant
import com.example.shockapp.API.NetworkResponse
import com.example.shockapp.API.RetrofitInstance
import com.example.shockapp.API.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel :ViewModel() {
    private val weatherAPI=RetrofitInstance.weatherAPI
    private val _weatherResult =MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> =_weatherResult
    fun getData(city: String){

        _weatherResult.value=NetworkResponse.Loading
        try {
            viewModelScope.launch {
                val response=weatherAPI.getWeather(Constant.APIkey,city)
                if(response.isSuccessful){
                    response.body()?.let {
                        _weatherResult.value=NetworkResponse.Success(it)
                    }
                }else{
                    _weatherResult.value=NetworkResponse.Failed(response.message())
                }
            }
        }catch (e :Exception){
            _weatherResult.value=NetworkResponse.Failed("failed to load Data")
        }
    }

    fun getDataByCoordinates(lat:Double,long:Double){
        _weatherResult.value=NetworkResponse.Loading
        viewModelScope.launch {
            try{
                val coordinates = "$lat,$long"
                val response=weatherAPI.getWeatherByCoordinates(Constant.APIkey,coordinates)
                if(response.isSuccessful){
                    response.body()?.let{
                        _weatherResult.value=NetworkResponse.Success(it)
                    }?:run {
                        _weatherResult.value=NetworkResponse.Failed("No Data Found")
                    }
                }else{
                    _weatherResult.value=NetworkResponse.Failed(response.message())
                }
            }catch (e:Exception){
                _weatherResult.value = NetworkResponse.Failed("Failed to load data: ${e.message}")
            }
        }
    }


}