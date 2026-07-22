package com.example.my_weather_forecast.presentation.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.example.my_weather_forecast.domain.model.WeatherIcon
import org.jetbrains.compose.resources.painterResource

/**
 * A weather icon tinted with its condition's accent color and given a small, cheap idle
 * animation via [rememberInfiniteTransition] + property transforms only — no path morphing, no
 * bitmap work, so it stays inexpensive on both platforms.
 */
@Composable
fun AnimatedWeatherIcon(
    icon: WeatherIcon,
    isDaytime: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val painter = painterResource(icon.toDrawableResource())
    val tint = icon.accentColor(isDaytime)
    val transition = rememberInfiniteTransition(label = "weatherIconMotion")

    when {
        icon == WeatherIcon.CLEAR && isDaytime -> {
            val rotation by transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing)),
                label = "sunRotation",
            )
            Icon(painter, contentDescription, tint = tint, modifier = modifier.graphicsLayer { rotationZ = rotation })
        }

        icon == WeatherIcon.CLEAR && !isDaytime -> {
            val scale by transition.animateFloat(
                initialValue = 0.92f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(tween(2_200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "moonPulse",
            )
            Icon(painter, contentDescription, tint = tint, modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale })
        }

        icon == WeatherIcon.CLOUDS -> {
            val drift by transition.animateFloat(
                initialValue = -1.5f,
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(tween(3_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "cloudDrift",
            )
            Icon(painter, contentDescription, tint = tint, modifier = modifier.graphicsLayer { translationX = drift })
        }

        icon == WeatherIcon.RAIN || icon == WeatherIcon.DRIZZLE || icon == WeatherIcon.SNOW -> {
            val fall by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
                label = "precipitationFall",
            )
            var size by remember { mutableStateOf(IntSize.Zero) }
            Box(modifier = modifier.onSizeChanged { size = it }) {
                Icon(painter, contentDescription, tint = tint)
                Canvas(modifier = Modifier) {
                    if (size.width == 0) return@Canvas
                    val dotRadius = this.size.minDimension * 0.06f
                    val startY = this.size.height * 0.55f
                    val endY = this.size.height * 0.95f
                    val y = startY + (endY - startY) * fall
                    val alpha = 1f - fall
                    drawCircle(
                        color = tint,
                        radius = dotRadius,
                        center = Offset(this.size.width * 0.7f, y),
                        alpha = alpha,
                    )
                }
            }
        }

        icon == WeatherIcon.THUNDERSTORM -> {
            val flicker by transition.animateFloat(
                initialValue = 1f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = 4_000
                        1f at 0
                        1f at 3_400
                        0.4f at 3_500
                        1f at 3_650
                        1f at 4_000
                    },
                ),
                label = "stormFlicker",
            )
            Icon(painter, contentDescription, tint = tint, modifier = modifier.graphicsLayer { alpha = flicker })
        }

        else -> Icon(painter, contentDescription, tint = tint, modifier = modifier)
    }
}
