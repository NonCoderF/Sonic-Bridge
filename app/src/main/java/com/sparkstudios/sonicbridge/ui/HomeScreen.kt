package com.sparkstudios.sonicbridge.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sparkstudios.sonicbridge.ui.components.ConnectButton
import com.sparkstudios.sonicbridge.ui.components.SonicBridgeHeader
import com.sparkstudios.sonicbridge.ui.components.StatusCard

@Composable
fun HomeScreen(

    uiState: HomeUiState,

    onConnectClick: () -> Unit,

    onChangeTv: () -> Unit

) {

    Surface(

        modifier = Modifier.fillMaxSize(),

        color = Color(0xFF0B0F17)

    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0F17))
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(
                    horizontal = 24.dp,
                    vertical = 20.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Top

        ) {

            Spacer(modifier = Modifier.height(12.dp))

            SonicBridgeHeader()

            Spacer(modifier = Modifier.height(36.dp))

            ConnectButton(

                isConnected = uiState.isConnected,

                onClick = onConnectClick

            )

            Spacer(modifier = Modifier.height(40.dp))

            StatusCard(

                isConnected = uiState.isConnected,

                serverIp = uiState.serverIp,

                bytesReceived = uiState.bytesReceived,

                transferRate = uiState.transferRate,

                latency = uiState.latency,

                audioFormat = uiState.audioFormat

            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(

                onClick = onChangeTv,

                contentPadding = PaddingValues(0.dp)

            ) {

                Text(
                    text = "Change TV",
                    color = MaterialTheme.colorScheme.primary
                )

            }

            Spacer(modifier = Modifier.weight(1f))

            Text(

                text = "Version 1.0",

                style = MaterialTheme.typography.bodySmall,

                color = Color.Gray

            )

            Spacer(modifier = Modifier.height(16.dp))

        }

    }

}