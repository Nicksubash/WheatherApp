package com.example.shockapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shockapp.API.Constant
import com.example.shockapp.API.NetworkResponse
import com.example.shockapp.API.RetrofitInstance
import com.example.shockapp.API.WeatherModel
import com.example.shockapp.API.data.AppDataBase
import com.example.shockapp.API.data.SearchHistory
import com.example.shockapp.API.data.SearchHistoryRepository
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    // Weather API and response handling
    private val weatherAPI = RetrofitInstance.weatherAPI
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    // Search History Repository
    private val searchHistoryRepository: SearchHistoryRepository

    fun deleteHistoryItem(history: SearchHistory){
        viewModelScope.launch {
            searchHistoryRepository.deleteHistoryItem(history)
        }
    }

    // Expose search history as a Flow (or you can convert to LiveData)
    val searchHistory = run {
        val dao = AppDataBase.getDatabase(application).searchHistoryDao()
        SearchHistoryRepository(dao).searchHistory
    }

    init {
        // Optionally, you can initialize the repository in the init block if preferred.
        val dao = AppDataBase.getDatabase(application).searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(dao)
    }

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherAPI.getWeather(Constant.APIkey, city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                        // Save the search query to your local database
                        searchHistoryRepository.addSearchQuery(city)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Failed(response.message())
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Failed("Failed to load Data")
            }
        }
    }

    fun getDataByCoordinates(lat: Double, long: Double) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val coordinates = "$lat,$long"
                val response = weatherAPI.getWeatherByCoordinates(Constant.APIkey, coordinates)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    } ?: run {
                        _weatherResult.value = NetworkResponse.Failed("No Data Found")
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Failed(response.message())
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Failed("Failed to load data: ${e.message}")
            }
        }
    }
}
