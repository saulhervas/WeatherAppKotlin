package com.saulhervas.weatherapp.domain.repository

import com.saulhervas.weatherapp.domain.api.WeatherApi
import com.saulhervas.weatherapp.model.DailyForecastResponse
import com.saulhervas.weatherapp.model.ForecastResponse
import com.saulhervas.weatherapp.model.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    suspend fun getWeatherByCity(cityName: String): Result<WeatherResponse> {
        return try {
            val response = weatherApi.getWeatherByCity(cityName)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val response = weatherApi.getWeatherByLocation(lat, lon)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getForecastByCity(cityName: String): Result<ForecastResponse> {
        return try {
            val response = weatherApi.getForecastByCity(cityName)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getForecastByLocation(lat: Double, lon: Double): Result<ForecastResponse> {
        return try {
            val response = weatherApi.getForecastByLocation(lat, lon)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyForecastByCity(cityName: String): Result<DailyForecastResponse> {
        return try {
            val response = weatherApi.getDailyForecastByCity(cityName)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyForecastByLocation(lat: Double, lon: Double): Result<DailyForecastResponse> {
        return try {
            val response = weatherApi.getDailyForecastByLocation(lat, lon)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 