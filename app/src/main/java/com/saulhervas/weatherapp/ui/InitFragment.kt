package com.saulhervas.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.saulhervas.weatherapp.ui.viewmodel.WeatherViewModel
import com.saulhervas.weatherapp.adapter.ForecastAdapter
import com.saulhervas.weatherapp.adapter.DailyForecastAdapter
import com.saulhervas.weatherapp.databinding.FragmentInitBinding
import com.saulhervas.weatherapp.model.HourlyForecast
import com.saulhervas.weatherapp.model.ForecastResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InitFragment : Fragment() {

    private lateinit var binding: FragmentInitBinding
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val forecastAdapter = ForecastAdapter()
    private val dailyForecastAdapter = DailyForecastAdapter()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            viewModel.getWeatherAndForecastByCity("Madrid")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupLocationClient()
        checkLocationPermission()
    }

    private fun setupRecyclerView() {
        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = forecastAdapter
        }

        binding.rvDailyForecast.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dailyForecastAdapter
        }
    }

    private fun updateWeatherIcon(weatherDescription: String) {
        Log.d("InitFragment", "Updating weather icon for: $weatherDescription")
        val iconResource = when {
            weatherDescription.contains("clear") || weatherDescription.contains("sun") -> {
                Log.d("InitFragment", "Setting sunny icon")
                com.saulhervas.weatherapp.R.drawable.sunny
            }
            weatherDescription.contains("cloud") || weatherDescription.contains("mist") -> {
                Log.d("InitFragment", "Setting cloudy_sunny icon")
                com.saulhervas.weatherapp.R.drawable.cloudy
            }
            weatherDescription.contains("rain") || weatherDescription.contains("drizzle") -> {
                Log.d("InitFragment", "Setting rainy icon")
                com.saulhervas.weatherapp.R.drawable.rainy
            }
            weatherDescription.contains("snow") || weatherDescription.contains("sleet") -> {
                Log.d("InitFragment", "Setting snowy icon")
                com.saulhervas.weatherapp.R.drawable.snowy
            }
            else -> {
                Log.d("InitFragment", "Setting default cloudy_sunny icon")
                com.saulhervas.weatherapp.R.drawable.cloudy_sunny
            }
        }
        try {
            requireActivity().runOnUiThread {
                binding.ivIcon.setImageResource(iconResource)
                Log.d("InitFragment", "Icon updated successfully")
            }
        } catch (e: Exception) {
            Log.e("InitFragment", "Error setting icon: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weatherData.collect { weather ->
                weather?.let {
                    Log.d("InitFragment", "Received weather data: $it")
                    binding.apply {
                        tvCity.text = it.cityName
                        tvTemperature.text = "${it.main.temp.toInt()}°C"
                        tvHumidity.text = "${it.main.humidity}%"
                        tvWind.text = "${it.wind.speed}m/s"
                        
                        // Actualizar el icono principal según el clima
                        val weatherDescription = it.weather.firstOrNull()?.description?.lowercase() ?: ""
                        Log.d("InitFragment", "Weather description: $weatherDescription")
                        updateWeatherIcon(weatherDescription)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.forecastData.collect { forecast ->
                Log.d("InitFragment", "Received forecast: $forecast")
                if (forecast == null) {
                    Log.d("InitFragment", "Forecast is null")
                    return@collect
                }
                
                try {
                    val forecastList = forecast.list
                    Log.d("InitFragment", "Forecast list size: ${forecastList.size}")
                    
                    if (forecastList.isEmpty()) {
                        Log.d("InitFragment", "Forecast list is empty")
                        return@collect
                    }
                    
                    // Actualizar el primer RecyclerView con los pronósticos por hora
                    Log.d("InitFragment", "Processing ${forecastList.size} hourly forecasts")
                    forecastAdapter.updateForecasts(forecastList)
                    
                    // Agrupar los pronósticos por día para el segundo RecyclerView
                    val dailyForecasts = forecastList.groupBy { hourlyForecast ->
                        val date = Date(hourlyForecast.date * 1000)
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    }.map { (_, forecasts) ->
                        // Para cada día, tomar el pronóstico con la temperatura máxima
                        forecasts.maxByOrNull { it.main.tempMax } ?: forecasts.first()
                    }
                    
                    Log.d("InitFragment", "Processing ${dailyForecasts.size} daily forecasts")
                    dailyForecastAdapter.updateForecasts(dailyForecasts)
                    
                    // Mostrar la probabilidad de lluvia del primer pronóstico
                    forecastList.firstOrNull()?.let { firstForecast ->
                        Log.d("InitFragment", "First forecast: $firstForecast")
                        binding.tvrain.text = "${(firstForecast.probabilityOfPrecipitation * 100).toInt()}%"
                    }
                } catch (e: Exception) {
                    Log.e("InitFragment", "Error processing forecast data: ${e.message}")
                    e.printStackTrace()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun checkLocationPermission() {
        when {
            hasLocationPermission() -> getCurrentLocation()
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                viewModel.getWeatherAndForecastByCity("Madrid")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (hasLocationPermission()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.getWeatherAndForecastByLocation(it.latitude, it.longitude)
                } ?: viewModel.getWeatherAndForecastByCity("Madrid")
            }.addOnFailureListener {
                viewModel.getWeatherAndForecastByCity("Madrid")
            }
        }
    }
}