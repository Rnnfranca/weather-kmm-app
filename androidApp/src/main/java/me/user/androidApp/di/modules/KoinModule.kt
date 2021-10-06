package me.user.androidApp.di.modules

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import me.user.androidApp.view.adapter.ForecastAdapter
import me.user.shared.viewmodel.OverviewViewModel
import org.koin.dsl.module.module

val uiModule = module {
    factory { ForecastAdapter(listOf()) }
    factory { OverviewViewModel() }
    factory { (context: Context) -> FusedLocationProviderClient(context) }
    factory { LocationRequest.create() }
}