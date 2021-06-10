package me.user.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("coord")
    val coord: Coord,
    @SerialName("weather")
    val weather: List<Weather>,
    @SerialName("main")
    val main: Main,
    @SerialName("name")
    val name: String,
    @SerialName("sys")
    val sys: Sys
)

@Serializable
data class ForecastResponse(
    @SerialName("daily")
    val daily: List<Daily>,
)

@Serializable
data class Daily(
    @SerialName("dt")
    val dateTimeStamp: Long,
    @SerialName("temp")
    val temp: Temp,
    @SerialName("weather")
    val weather: List<ForeCastWeather>,

)

@Serializable
data class ForeCastWeather(
    @SerialName("icon")
    val icon: String
)

@Serializable
data class Temp(
    @SerialName("max")
    val max: Double,
    @SerialName("min")
    val min: Double
)

@Serializable
data class Coord (
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double
)

@Serializable
data class Weather(
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String
)

@Serializable
data class Main(
    @SerialName("temp")
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    @SerialName("humidity")
    val humidity: Int
)

@Serializable
data class Sys(
    @SerialName("country")
    val country: String
)