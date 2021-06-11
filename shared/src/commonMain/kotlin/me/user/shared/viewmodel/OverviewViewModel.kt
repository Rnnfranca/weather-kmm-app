package me.user.shared.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.user.shared.coroutineDispatcher
import me.user.shared.model.ForecastResponse
import me.user.shared.model.WeatherResponse
import me.user.shared.repository.RepositoryAPI
import kotlin.coroutines.CoroutineContext


class OverviewViewModel {
    private val repositoryAPI = RepositoryAPI()

    private val coroutineContext: CoroutineContext get() = SupervisorJob() + coroutineDispatcher
    private val overviewViewModelScope = CoroutineScope(coroutineContext)
    private lateinit var job: Job

    private val _weatherResponse = MutableStateFlow<WeatherResponse?>(null)
    val weatherResponse: StateFlow<WeatherResponse?> get() = _weatherResponse

    private val _forecastResponse = MutableStateFlow<ForecastResponse?>(null)
    val forecastResponse: StateFlow<ForecastResponse?> get() = _forecastResponse

    fun getWeatherbyCoordinates(lat: String, lon: String) {
        job = overviewViewModelScope.launch {
            _weatherResponse.value = repositoryAPI.getWeatherbyCoordinates(lat, lon)
        }
    }

    fun getForecastByCoordinates(lat: String, lon: String) {
        job = overviewViewModelScope.launch {
            _forecastResponse.value = repositoryAPI.getForecastByCoordinates(lat, lon)
        }
    }

    suspend fun cancelScope() {
        job.cancelAndJoin()
    }

}