package com.sparkstudios.sonicbridge.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sparkstudios.sonicbridge.network.TvDevice

@Composable
fun AvailableTvsScreen(
    state: TvDiscoveryUiState,
    onRefresh: () -> Unit,
    onSelect: (TvDevice) -> Unit,
    onManualIp: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = Color(0xFF0B0F17)) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Available TVs", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Text("Select a TV on your Wi-Fi network", color = Color.LightGray)
                }
                IconButton(onClick = onRefresh, enabled = !state.isScanning) {
                    Icon(Icons.Rounded.Refresh, "Scan again", tint = Color(0xFF00C8FF))
                }
            }

            Spacer(Modifier.size(24.dp))
            if (state.isScanning && state.devices.isEmpty()) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF00C8FF))
                    Spacer(Modifier.size(12.dp))
                    Text("Scanning for SonicBridge TVs…", color = Color.LightGray)
                }
            } else if (state.devices.isEmpty()) {
                Text("No TVs found. Make sure the TV app is running and both devices use the same Wi-Fi.", color = Color.LightGray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.devices, key = { it.ip }) { device ->
                        Row(
                            Modifier.fillMaxWidth().clickable { onSelect(device) }.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Tv, null, tint = Color(0xFF00C8FF), modifier = Modifier.size(36.dp))
                            Column(Modifier.padding(start = 16.dp)) {
                                Text(device.name, color = Color.White, style = MaterialTheme.typography.titleMedium)
                                Text(device.ip, color = Color.LightGray)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Button(onClick = onManualIp, Modifier.fillMaxWidth()) { Text("Enter IP manually") }
        }
    }
}
