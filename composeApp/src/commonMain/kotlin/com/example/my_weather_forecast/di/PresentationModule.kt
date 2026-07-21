package com.example.my_weather_forecast.di

import com.example.my_weather_forecast.presentation.detail.DetailViewModel
import com.example.my_weather_forecast.presentation.overview.OverviewViewModel
import com.example.my_weather_forecast.presentation.search.SearchViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

// org.koin.compose.viewmodel.dsl.viewModel is marked deprecated in koin-compose-viewmodel
// 4.1.0 pending a merged org.koin.core.module.dsl.viewModel that isn't published yet for
// this pinned version; it's still the correct way to register a ViewModel-lifecycle-aware
// definition that koinViewModel() can resolve.
@Suppress("DEPRECATION")
val presentationModule = module {
    viewModel { OverviewViewModel(get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { (locationId: Long) -> DetailViewModel(locationId, get(), get()) }
}
