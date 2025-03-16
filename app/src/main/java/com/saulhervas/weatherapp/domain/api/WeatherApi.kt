package com.saulhervas.weatherapp.domain.api

import com.saulhervas.weatherapp.model.DailyForecastResponse
import com.saulhervas.weatherapp.model.ForecastResponse
import com.saulhervas.weatherapp.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getForecastByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponse>

    @GET("forecast")
    suspend fun getForecastByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponse>

    @GET("forecast/daily")
    suspend fun getDailyForecastByCity(
        @Query("q") cityName: String,
        @Query("cnt") count: Int = 7,
        @Query("units") units: String = "metric"
    ): Response<DailyForecastResponse>

    @GET("forecast/daily")
    suspend fun getDailyForecastByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") count: Int = 7,
        @Query("units") units: String = "metric"
    ): Response<DailyForecastResponse>
} 