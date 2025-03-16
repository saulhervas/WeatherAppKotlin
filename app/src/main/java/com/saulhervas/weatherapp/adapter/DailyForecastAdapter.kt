package com.saulhervas.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saulhervas.weatherapp.databinding.ItemDailyForecastBinding
import com.saulhervas.weatherapp.model.DailyForecast

class DailyForecastAdapter : RecyclerView.Adapter<DailyForecastViewHolder>() {

    private var forecasts: List<DailyForecast> = emptyList()

    fun updateForecasts(newForecasts: List<DailyForecast>) {
        forecasts = newForecasts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding = ItemDailyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        holder.bind(forecasts[position])
    }

    override fun getItemCount(): Int = forecasts.size
} 