package com.saulhervas.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saulhervas.weatherapp.databinding.ItemForecastBinding
import com.saulhervas.weatherapp.model.HourlyForecast

class ForecastAdapter(private val maxItems: Int = 24) : RecyclerView.Adapter<WeatherViewHolder>() {

    private var forecasts: List<HourlyForecast> = emptyList()

    fun updateForecasts(newForecasts: List<HourlyForecast>) {
        println("Updating forecasts with ${newForecasts.size} items")
        forecasts = newForecasts.take(maxItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        println("Creating new ViewHolder")
        val binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        println("Binding ViewHolder at position $position")
        holder.bind(forecasts[position])
    }

    override fun getItemCount(): Int = forecasts.size


} 