package com.sparkstudios.sonicbridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun IpSetupScreen(
    onContinue: (String) -> Unit
) {

    var ip by remember {
        mutableStateOf("")
    }

    val isValid =
        remember(ip) {
            isValidIp(ip)
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B0F17)
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center

        ) {

            Icon(
                imageVector = Icons.Rounded.Tv,
                contentDescription = null,
                tint = Color(0xFF00C8FF),
                modifier = Modifier.size(72.dp)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Welcome to SonicBridge",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Enter your Android TV IP Address",
                color = Color.LightGray
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            OutlinedTextField(

                modifier = Modifier.fillMaxWidth(),

                value = ip,

                onValueChange = {
                    ip = it
                },

                singleLine = true,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                label = {
                    Text("TV IP Address")
                },

                placeholder = {
                    Text("192.168.1.16")
                },

                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(

                    focusedContainerColor = Color(0xFF1E2636),

                    unfocusedContainerColor = Color(0xFF1E2636),

                    focusedBorderColor = Color(0xFF00C8FF),

                    unfocusedBorderColor = Color(0xFF455067),

                    focusedTextColor = Color.White,

                    unfocusedTextColor = Color.White,

                    cursorColor = Color(0xFF00C8FF),

                    focusedLabelColor = Color(0xFF00C8FF),

                    unfocusedLabelColor = Color.LightGray,

                    focusedPlaceholderColor = Color.Gray,

                    unfocusedPlaceholderColor = Color.Gray

                )

            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(

                enabled = isValid,

                onClick = {

                    onContinue(ip)

                }

            ) {

                Text("Continue")

            }

        }

    }

}

private fun isValidIp(
    ip: String
): Boolean {

    return Regex(
        "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$"
    ).matches(ip)

}