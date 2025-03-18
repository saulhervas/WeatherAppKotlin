package com.saulhervas.weatherapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.saulhervas.weatherapp.databinding.ItemForecastBinding
import com.saulhervas.weatherapp.model.HourlyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherViewHolder(
    private val binding: ItemForecastBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun bind(forecast: HourlyForecast) {
        val date = Date(forecast.date * 1000)
        binding.apply {
            hourTxt.text = dateFormat.format(date)
            tempTxt.text = "${forecast.main.temp.toInt()}°"
            
            // Mostrar la probabilidad de lluvia como porcentaje
            val rainProbability = (forecast.probabilityOfPrecipitation * 100).toInt()
            rainProbabilityTxt.text = "$rainProbability%"

            // Determinar qué icono mostrar basado en la descripción del clima
            val weatherDescription = forecast.weather.firstOrNull()?.main?.lowercase() ?: ""
            println("Weather description: $weatherDescription")
            val iconResource = when {
                weatherDescription.contains("clear") -> com.saulhervas.weatherapp.R.drawable.sunny
                weatherDescription.contains("cloud") -> com.saulhervas.weatherapp.R.drawable.cloudy
                weatherDescription.contains("rain") -> com.saulhervas.weatherapp.R.drawable.rainy
                weatherDescription.contains("snow") -> com.saulhervas.weatherapp.R.drawable.snowy
                else -> com.saulhervas.weatherapp.R.drawable.cloudy_sunny
            }
            iconWeatherImg.setImageResource(iconResource)
        }
    }
}