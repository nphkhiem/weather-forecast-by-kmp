package com.example.my_weather_forecast.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.back
import myweatherforecast.composeapp.generated.resources.ic_back
import myweatherforecast.composeapp.generated.resources.settings_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val units by viewModel.units.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.back))
                    }
                },
            )
        },
    ) { paddingValues ->
        SettingsContent(
            units = units,
            themeMode = themeMode,
            onUnitsSelected = viewModel::setUnits,
            onThemeModeSelected = viewModel::setThemeMode,
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
        )
    }
}
