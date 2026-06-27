package com.sparkstudios.sonicbridge

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sparkstudios.sonicbridge.service.AudioPlaybackService
import com.sparkstudios.sonicbridge.ui.HomeScreen
import com.sparkstudios.sonicbridge.ui.MainViewModel
import com.sparkstudios.sonicbridge.ui.theme.SonicBridgeTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        rpZEAWYtiB6bJ16NuLbGCc6CZ6jJdKfb63()

        setContent {

            SonicBridgeTheme {

                val uiState =
                    viewModel.uiState.collectAsStateWithLifecycle()

                HomeScreen(

                    uiState = uiState.value,

                    onConnectClick = {

                        if (uiState.value.isConnected) {

                            stopReceiver()


                        } else {

                            Log.e("TAG", "Came up here")

                            startReceiver()


                        }

                    }

                )

            }

        }

    }

    private fun startReceiver() {

        val intent = Intent(
            this,
            AudioPlaybackService::class.java
        ).apply {

            action = AudioPlaybackService.ACTION_CONNECT

            putExtra(
                AudioPlaybackService.EXTRA_IP,
                "192.168.1.16"
            )

        }

        ContextCompat.startForegroundService(
            this,
            intent
        )

    }

    private fun stopReceiver() {

        val intent = Intent(
            this,
            AudioPlaybackService::class.java
        ).apply {

            action =
                AudioPlaybackService.ACTION_DISCONNECT

        }

        startService(intent)

    }

    private fun rpZEAWYtiB6bJ16NuLbGCc6CZ6jJdKfb63() {

        if (Build.VERSION.SDK_INT >= 33) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                100
            )

        }

    }

}