package com.saulhervas.weatherapp.model

import com.google.gson.annotations.SerializedName

data class DailyForecastResponse(
    @SerializedName("list")
    val list: List<DailyForecast>,
    @SerializedName("city")
    val city: City
)

data class DailyForecast(
    @SerializedName("dt")
    val date: Long,
    @SerializedName("temp")
    val temperature: DailyTemperature,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("pop")
    val probabilityOfPrecipitation: Double
)

data class DailyTemperature(
    @SerializedName("day")
    val day: Double,
    @SerializedName("min")
    val min: Double,
    @SerializedName("max")
    val max: Double,
    @SerializedName("night")
    val night: Double,
    @SerializedName("eve")
    val evening: Double,
    @SerializedName("morn")
    val morning: Double
) 