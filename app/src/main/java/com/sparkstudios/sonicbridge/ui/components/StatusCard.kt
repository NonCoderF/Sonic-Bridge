package com.sparkstudios.sonicbridge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.NetworkWifi
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing

@Composable
fun StatusCard(

    isConnected: Boolean,

    serverIp: String,

    bytesReceived: Long,

    transferRate: String,

    latency: Int,

    audioFormat: String,

    waveform: List<Float>

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF171C26)

        ),

        shape = RoundedCornerShape(20.dp)

    ) {

        Column(

            modifier = Modifier.padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            Text(

                text = "Connection Details",

                style = MaterialTheme.typography.titleLarge,

                color = Color.White

            )

            Waveform(isActive = isConnected, amplitudes = waveform)

            HorizontalDivider()

            StatusRow(

                icon = {

                    Icon(

                        Icons.Rounded.NetworkWifi,

                        null,

                        tint = if (isConnected)
                            Color(0xFF4CAF50)
                        else
                            Color.Gray

                    )

                },

                title = "Status",

                value = if (isConnected)
                    "Connected"
                else
                    "Disconnected"

            )

            StatusRow(

                icon = {

                    Icon(

                        Icons.Rounded.NetworkWifi,

                        null,

                        tint = Color(0xFF00C8FF)

                    )

                },

                title = "TV Address",

                value = serverIp

            )

            StatusRow(

                icon = {

                    Icon(

                        Icons.Rounded.Storage,

                        null,

                        tint = Color.White

                    )

                },

                title = "Bytes Received",

                value = formatBytes(bytesReceived)

            )

            StatusRow(

                icon = {

                    Icon(

                        Icons.Rounded.Speed,

                        null,

                        tint = Color(0xFFFFC107)

                    )

                },

                title = "Transfer Rate",

                value = transferRate

            )

            StatusRow(

                icon = {

                    Icon(

                        Icons.Rounded.GraphicEq,

                        null,

                        tint = Color(0xFF00E676)

                    )

                },

                title = "Audio",

                value = audioFormat

            )

            StatusRow(

                icon = {

                    Icon(

                        Icons.Rounded.Speed,

                        null,

                        tint = Color(0xFF00E676)

                    )

                },

                title = "Latency",

                value = "$latency ms"

            )

        }

    }

}

@Composable
private fun StatusRow(

    icon: @Composable () -> Unit,

    title: String,

    value: String

) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Row(

            verticalAlignment = Alignment.CenterVertically

        ) {

            icon()

            Text(

                modifier = Modifier.padding(start = 12.dp),

                text = title,

                color = Color.LightGray,

                style = MaterialTheme.typography.bodyLarge

            )

        }

        Text(

            text = value,

            color = Color.White,

            style = MaterialTheme.typography.bodyLarge

        )

    }

}

private fun formatBytes(bytes: Long): String {

    return when {

        bytes >= 1024L * 1024L ->
            String.format("%.2f MB", bytes / 1024f / 1024f)

        bytes >= 1024L ->
            String.format("%.2f KB", bytes / 1024f)

        else ->
            "$bytes B"

    }

}

@Composable
private fun Waveform(isActive: Boolean, amplitudes: List<Float>) {
    val animatedAmplitudes = amplitudes.map { amplitude ->
        animateFloatAsState(
            targetValue = if (isActive) amplitude.coerceIn(0f, 1f) else 0.02f,
            animationSpec = tween(16, easing = LinearEasing),
            label = "waveform-bar"
        ).value
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .background(Color(0xFF101722), RoundedCornerShape(16.dp))
    ) {
        Canvas(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 16.dp)) {
            val bars = thirtyTwo
            val gap = 4.dp.toPx()
            val barWidth = (size.width - gap * (bars - 1)) / bars
            val center = size.height / 2f
            for (index in 0 until bars) {
                val wave = animatedAmplitudes.getOrNull(index) ?: 0.02f
                val visualWave = wave.coerceIn(0.02f, 1f)
                val height = size.height * (0.02f + visualWave * 0.98f)
                val intensity = visualWave.coerceIn(0f, 1f)
                val barColor = if (intensity < 0.5f) {
                    lerp(Color(0xFF00E676), Color(0xFFFFD600), intensity * 2f)
                } else {
                    lerp(Color(0xFFFFD600), Color(0xFFFF1744), (intensity - 0.5f) * 2f)
                }
                drawRoundRect(
                    color = if (isActive) barColor else Color(0xFF455067),
                    topLeft = androidx.compose.ui.geometry.Offset(index * (barWidth + gap), center - height / 2),
                    size = androidx.compose.ui.geometry.Size(barWidth, height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth, barWidth)
                )
            }
        }
    }
}

private const val thirtyTwo = 32
