package com.saulhervas.weatherapp.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.saulhervas.weatherapp.R
import com.saulhervas.weatherapp.databinding.ItemDailyForecastBinding
import com.saulhervas.weatherapp.model.HourlyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyForecastViewHolder(
    private val binding: ItemDailyForecastBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(forecast: HourlyForecast) {
        // Convertir timestamp a fecha
        val date = Date(forecast.date * 1000)
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val day = dayFormat.format(date)
        binding.dayTxt.text = day

        // Mostrar temperaturas
        binding.maxTempTxt.text = "${forecast.main.tempMax.toInt()}°"
        binding.minTempTxt.text = "${forecast.main.tempMin.toInt()}°"

        // Mostrar probabilidad de lluvia
        val rainProb = (forecast.probabilityOfPrecipitation * 100).toInt()
        binding.rainProbabilityTxt.text = "$rainProb%"
        binding.rainIcon.visibility = if (rainProb > 0) View.VISIBLE else View.GONE

        // Actualizar icono según el clima
        val weatherDescription = forecast.weather.firstOrNull()?.description?.lowercase() ?: ""
        val iconResource = when {
            weatherDescription.contains("clear") || weatherDescription.contains("sun") -> R.drawable.sunny
            weatherDescription.contains("cloud") -> R.drawable.cloudy
            weatherDescription.contains("rain") || weatherDescription.contains("drizzle") -> R.drawable.rainy
            weatherDescription.contains("snow") || weatherDescription.contains("sleet") -> R.drawable.snowy
            else -> R.drawable.cloudy_sunny
        }
        binding.iconWeatherImg.setImageResource(iconResource)
    }
} 