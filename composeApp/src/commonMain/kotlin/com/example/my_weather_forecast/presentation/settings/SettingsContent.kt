package com.example.my_weather_forecast.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.domain.model.Units
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.theme_dark
import myweatherforecast.composeapp.generated.resources.theme_light
import myweatherforecast.composeapp.generated.resources.theme_section_label
import myweatherforecast.composeapp.generated.resources.theme_system
import myweatherforecast.composeapp.generated.resources.units_imperial
import myweatherforecast.composeapp.generated.resources.units_metric
import myweatherforecast.composeapp.generated.resources.units_section_label
import myweatherforecast.composeapp.generated.resources.selected_checkmark
import org.jetbrains.compose.resources.stringResource

const val SETTINGS_CONTENT_TEST_TAG = "settings_content"

@Composable
fun SettingsContent(
    units: Units,
    themeMode: ThemeMode,
    onUnitsSelected: (Units) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(SETTINGS_CONTENT_TEST_TAG)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        SettingsSection(title = stringResource(Res.string.theme_section_label)) {
            ThemeMode.entries.forEach { mode ->
                SettingsOptionRow(
                    label = stringResource(mode.labelRes()),
                    selected = mode == themeMode,
                    onClick = { onThemeModeSelected(mode) },
                )
            }
        }
        SettingsSection(title = stringResource(Res.string.units_section_label)) {
            SettingsOptionRow(
                label = stringResource(Res.string.units_metric),
                selected = units == Units.METRIC,
                onClick = { onUnitsSelected(Units.METRIC) },
            )
            SettingsOptionRow(
                label = stringResource(Res.string.units_imperial),
                selected = units == Units.IMPERIAL,
                onClick = { onUnitsSelected(Units.IMPERIAL) },
            )
        }
    }
}

@Composable
private fun ThemeMode.labelRes() = when (this) {
    ThemeMode.SYSTEM -> Res.string.theme_system
    ThemeMode.LIGHT -> Res.string.theme_light
    ThemeMode.DARK -> Res.string.theme_dark
}

@Composable
private fun SettingsSection(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        content()
    }
}

@Composable
private fun SettingsOptionRow(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        if (selected) {
            Text(text = stringResource(Res.string.selected_checkmark), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
