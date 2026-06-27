package com.sparkstudios.sonicbridge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusCard(

    isConnected: Boolean,

    serverIp: String,

    bytesReceived: Long,

    transferRate: String,

    latency: Int,

    audioFormat: String

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