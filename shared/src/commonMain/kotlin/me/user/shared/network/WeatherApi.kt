package me.user.shared.network

import io.ktor.client.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import me.user.shared.model.ForecastResponse
import me.user.shared.model.WeatherResponse

class WeatherApi {
    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    companion object {
        val UNIT = "metric"
        val LANG = "en"
        const val APPID = "25c534b5506a4990eb1e974c389bec59"


    }

    suspend fun getWeatherByCoordinates(lat: String, lon: String): WeatherResponse {
        val endpoint =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=$UNIT&lang=$LANG&appid=$APPID"

        return httpClient.get(endpoint)
    }

    suspend fun getForecastByCoordinates(lat: String, lon: String): ForecastResponse {
        val endpoint =
            "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&units=$UNIT&exclude=current,minutely,hourly,alerts&appid=$APPID"

        return httpClient.get(endpoint)
    }
}