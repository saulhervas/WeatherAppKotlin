package com.saulhervas.weatherapp.model

import com.google.gson.annotations.SerializedName

data class DailyForecastResponse(
    @SerializedName("list")
    val list: List<DailyForecast>
)

data class DailyForecast(
    @SerializedName("dt")
    val date: Long,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("pop")
    val probabilityOfPrecipitation: Double
)

