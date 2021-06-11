package me.user.androidApp.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.user.androidApp.databinding.FragmentOverviewBinding
import me.user.androidApp.view.adapter.ForecastAdapter
import me.user.shared.model.Daily
import me.user.shared.network.WeatherApi.Companion.UNIT
import me.user.shared.viewmodel.OverviewViewModel

class OverviewFragment : Fragment() {

    private val PERMISSION_ID = 1000
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private val overviewViewModel = OverviewViewModel()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: FrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mForecastAdapter: ForecastAdapter

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(inflater)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewConfiguration()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = binding.progressBar

        getLastLocation()

        collectWeatherResponse()
        collectForecastResponse()
    }

    private fun getLastLocation() {


        if (checkPermission()) {
            if (isLocationEnabled()) {
                binding.linearLayoutLocationWarning.visibility = View.INVISIBLE

                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result

                    if (location == null) {

                        getNewLocation()

                    } else {

                        overviewViewModel.getWeatherbyCoordinates(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                        overviewViewModel.getForecastByCoordinates(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )

                    }
                }
            } else {
                binding.linearLayoutLocationWarning.visibility = View.VISIBLE
                binding.linearLayoutContainer.visibility = View.INVISIBLE
                progressBar.isVisible = false

            }
        } else {
            requestPermission()
        }
    }

    // check user permission
    private fun checkPermission(): Boolean {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.linearLayoutContainer.visibility = View.VISIBLE
            binding.linearLayoutPermissionWarning.visibility = View.INVISIBLE
            return true
        }
        binding.linearLayoutContainer.visibility = View.INVISIBLE
        binding.linearLayoutPermissionWarning.visibility = View.VISIBLE

        return false
    }

    // Check if the location service of the device is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        progressBar.isVisible = false

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getNewLocation() {

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 2
        }
        if (checkPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation: Location = p0.lastLocation
            Log.d(
                "getLastLocation",
                "Lat: ${lastLocation.latitude}, Long: ${lastLocation.longitude}"
            )
            overviewViewModel.getWeatherbyCoordinates(
                lastLocation.latitude.toString(),
                lastLocation.longitude.toString()
            )
            overviewViewModel.getForecastByCoordinates(
                lastLocation.latitude.toString(),
                lastLocation.longitude.toString()
            )

        }
    }

    // function to request user permission
    private fun requestPermission() {

        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {


        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug", "You have the permission")
                binding.linearLayoutContainer.visibility = View.VISIBLE
                binding.linearLayoutPermissionWarning.visibility = View.INVISIBLE
                getLastLocation()

            } else {
                Log.d("Debug", "You don't have the permission")
                binding.linearLayoutContainer.visibility = View.INVISIBLE
                binding.linearLayoutPermissionWarning.visibility = View.VISIBLE
                progressBar.isVisible = false
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun collectForecastResponse() {
        lifecycleScope.launch {
            overviewViewModel.forecastResponse.collect { forecastResponse ->

                forecastResponse?.also {
                    val dailyList = mutableListOf<Daily>()

                    forecastResponse.daily.forEachIndexed { i, _ ->
                        if (i > 0) {
                            dailyList.add(forecastResponse.daily[i])
                        }
                    }

                    mForecastAdapter.updateForecast(dailyList)

                }

            }
        }
    }

    private fun collectWeatherResponse() {
        progressBar.isVisible = true
        lifecycleScope.launch {


            overviewViewModel.weatherResponse.collect { weatherResponse ->


                // se o banco de dados estiver vazio, incluir os dados aqui

                weatherResponse?.also {

                    Glide.with(binding.mainImage)
                        .load("http://openweathermap.org/img/wn/${it.weather[0].icon}.png")
                        .into(binding.mainImage)

                    binding.description.text = it.weather[0].description
                    binding.location.text = it.name.plus(", ${it.sys.country}")
                    binding.currentWeather.text = it.main.temp.toInt().toString()
                    binding.tempMax.text = it.main.tempMax.toInt().toString().plus("º")
                    binding.tempMin.text = it.main.tempMin.toInt().toString().plus("º")
                    binding.feelsLike.text = it.main.feelsLike.toInt().toString().plus("º")

                    when (UNIT) {
                        "metric" -> {
                            binding.unit.text = "ºC"
                        }
                        "imperial" -> {
                            binding.unit.text = "ºF"
                        }
                    }

                    progressBar.isVisible = false

                }

            }
        }

    }

    private fun viewConfiguration() {

        swipeRefreshLayout = binding.swipeRefresh

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            getLastLocation()
        }

        recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        mForecastAdapter = ForecastAdapter(listOf())
        recyclerView.adapter = mForecastAdapter


    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycleScope.launch(Dispatchers.IO) {
            overviewViewModel.cancelScope()

        }

        _binding = null
    }

}