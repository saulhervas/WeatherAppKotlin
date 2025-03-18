package com.saulhervas.weatherapp.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list")
    val list: List<HourlyForecast>,
    @SerializedName("city")
    val city: City
)

data class HourlyForecast(
    @SerializedName("dt")
    val date: Long,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("pop")
    val probabilityOfPrecipitation: Double,
    @SerializedName("rain")
    val rain: Rain? = null,
    @SerializedName("snow")
    val snow: Snow? = null,
    @SerializedName("dt_txt")
    val dateText: String
)

data class City(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("coord")
    val coordinates: Coordinates,
    @SerializedName("country")
    val country: String,
    @SerializedName("population")
    val population: Int,
    @SerializedName("timezone")
    val timezone: Int,
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long
)

data class Coordinates(
    @SerializedName("lon")
    val longitude: Double,
    @SerializedName("lat")
    val latitude: Double
)

data class Clouds(
    @SerializedName("all")
    val all: Int
)

data class Wind(
    @SerializedName("speed")
    val speed: Double? = null,
    @SerializedName("deg")
    val deg: Int? = null,
    @SerializedName("gust")
    val gust: Double? = null
)

data class Rain(
    @SerializedName("3h")
    val volumeForLastThreeHours: Double? = null
)

data class Snow(
    @SerializedName("3h")
    val volumeForLastThreeHours: Double? = null
) 