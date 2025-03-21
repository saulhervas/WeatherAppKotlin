package com.saulhervas.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saulhervas.weatherapp.domain.repository.WeatherRepository
import com.saulhervas.weatherapp.model.DailyForecastResponse
import com.saulhervas.weatherapp.model.ForecastResponse
import com.saulhervas.weatherapp.model.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    private val _forecastData = MutableStateFlow<ForecastResponse?>(null)
    val forecastData: StateFlow<ForecastResponse?> = _forecastData

    private val _dailyForecastData = MutableStateFlow<DailyForecastResponse?>(null)
    val dailyForecastData: StateFlow<DailyForecastResponse?> = _dailyForecastData

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getWeatherAndForecastByCity(cityName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getWeatherByCity(cityName).onSuccess { weather ->
                    _weatherData.value = weather
                }.onFailure { exception ->
                    _error.value = exception.message
                }

                repository.getForecastByCity(cityName).onSuccess { forecast ->
                    _forecastData.value = forecast
                }.onFailure { exception ->
                    _error.value = exception.message
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getWeatherAndForecastByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getWeatherByLocation(lat, lon).onSuccess { weather ->
                    _weatherData.value = weather
                }.onFailure { exception ->
                    _error.value = exception.message
                }

                repository.getForecastByLocation(lat, lon).onSuccess { forecast ->
                    _forecastData.value = forecast
                }.onFailure { exception ->
                    _error.value = exception.message
                }

                repository.getDailyForecastByLocation(lat, lon).onSuccess { dailyForecast ->
                    _dailyForecastData.value = dailyForecast
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
} 