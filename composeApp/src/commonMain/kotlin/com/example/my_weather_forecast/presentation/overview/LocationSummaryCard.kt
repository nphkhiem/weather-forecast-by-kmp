package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.presentation.theme.readableName
import com.example.my_weather_forecast.presentation.theme.toDrawableResource
import kotlin.math.roundToInt
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_delete
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSummaryCard(
    area: AreaSummary,
    onClick: () -> Unit,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove(area.id)
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = "Delete ${area.name}",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .semantics { contentDescription = area.accessibilityDescription() },
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(area.icon.toDrawableResource()),
                    contentDescription = area.icon.readableName(),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(40.dp),
                )
                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(area.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "H:${area.todayHigh.roundToInt()}°  L:${area.todayLow.roundToInt()}°  " +
                            "${(area.rainChance * 100).roundToInt()}% rain",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text("${area.currentTemp.roundToInt()}°", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

private fun AreaSummary.accessibilityDescription(): String {
    val staleNote = if (stale) ", data may be out of date" else ""
    return "$name, ${currentTemp.roundToInt()} degrees, high ${todayHigh.roundToInt()}, " +
        "low ${todayLow.roundToInt()}, ${(rainChance * 100).roundToInt()} percent chance of rain$staleNote"
}
