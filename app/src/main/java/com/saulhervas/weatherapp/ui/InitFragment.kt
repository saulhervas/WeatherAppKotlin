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
            // Si no hay permisos, usar una ciudad por defecto
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

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weatherData.collect { weather ->
                weather?.let {
                    binding.apply {
                        tvCity.text = it.cityName
                        tvTemperature.text = "${it.main.temp.toInt()}°C"
                        tvHumidity.text = "${it.main.humidity}%"
                        tvWind.text = "${it.wind.speed}m/s"
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
                    
                    Log.d("InitFragment", "Processing ${forecastList.size} forecasts")
                    forecastAdapter.updateForecasts(forecastList)
                    
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
            viewModel.dailyForecastData.collect { forecast ->
                Log.d("InitFragment", "Received daily forecast: $forecast")
                forecast?.let {
                    Log.d("InitFragment", "Daily forecast list size: ${it.list.size}")
                    dailyForecastAdapter.updateForecasts(it.list)
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
                // Mostrar diálogo explicando por qué necesitamos el permiso
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