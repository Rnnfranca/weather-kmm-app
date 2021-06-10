package me.user.shared.repository

import me.user.shared.model.ForecastResponse
import me.user.shared.model.WeatherResponse
import me.user.shared.network.WeatherApi

class RepositoryAPI {
    private val api = WeatherApi()

    suspend fun getWeatherbyCoordinates(lat: String, lon: String): WeatherResponse {
        return api.getWeatherByCoordinates(lat, lon)
    }

    suspend fun getForecastByCoordinates(lat: String, lon: String): ForecastResponse {
        return api.getForecastByCoordinates(lat, lon)
    }

}