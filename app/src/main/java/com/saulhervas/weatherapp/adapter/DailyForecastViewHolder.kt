package com.saulhervas.weatherapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.saulhervas.weatherapp.databinding.ItemDailyForecastBinding
import com.saulhervas.weatherapp.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyForecastViewHolder(
    private val binding: ItemDailyForecastBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    fun bind(forecast: DailyForecast) {
        val date = Date(forecast.date * 1000)
        binding.apply {
            dayTxt.text = dateFormat.format(date)
            maxTempTxt.text = "${forecast.temperature.max.toInt()}°"
            minTempTxt.text = "${forecast.temperature.min.toInt()}°"
            
            // Mostrar la probabilidad de lluvia como porcentaje
            val rainProbability = (forecast.probabilityOfPrecipitation * 100).toInt()
            rainProbabilityTxt.text = "$rainProbability%"

            // Determinar qué icono mostrar basado en la descripción del clima
            val weatherDescription = forecast.weather.firstOrNull()?.main?.lowercase() ?: ""
            println("Weather description for daily forecast: $weatherDescription")
            val iconResource = when {
                weatherDescription.contains("clear") -> com.saulhervas.weatherapp.R.drawable.sunny
                weatherDescription.contains("cloud") -> com.saulhervas.weatherapp.R.drawable.cloudy_sunny
                weatherDescription.contains("rain") -> com.saulhervas.weatherapp.R.drawable.rainy
                weatherDescription.contains("snow") -> com.saulhervas.weatherapp.R.drawable.snowy
                else -> com.saulhervas.weatherapp.R.drawable.cloudy_sunny
            }
            iconWeatherImg.setImageResource(iconResource)
        }
    }
} 