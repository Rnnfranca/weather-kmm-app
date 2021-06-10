package me.user.androidApp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.user.androidApp.databinding.ForecastItemBinding
import me.user.shared.model.Daily
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter(private var forecast: List<Daily>) :
    RecyclerView.Adapter<ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val forecastItemBinding =
            ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ForecastViewHolder(forecastItemBinding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {

        holder.bind(forecast[position])

    }

    override fun getItemCount(): Int {
        return forecast.size
    }

    fun updateForecast(forecast: List<Daily>) {
        this.forecast = forecast
        notifyDataSetChanged()
    }

}

class ForecastViewHolder(private val binding: ForecastItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(daily: Daily) {

        val format = SimpleDateFormat("dd-MM", Locale.US)
        val date = Date(daily.dateTimeStamp * 1000)
        val dateString = format.format(date)


        binding.forecastDate.text = dateString
        Glide.with(binding.forecastImage)
            .load("http://openweathermap.org/img/wn/${daily.weather[0].icon}.png")
            .into(binding.forecastImage)
        binding.forecastTempMax.text = daily.temp.max.toInt().toString().plus("º")
        binding.forecastTempMin.text = daily.temp.min.toInt().toString().plus("º")
    }

}