package com.sparkstudios.sonicbridge

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sparkstudios.sonicbridge.data.Prefs
import com.sparkstudios.sonicbridge.service.AudioPlaybackService
import com.sparkstudios.sonicbridge.ui.HomeScreen
import com.sparkstudios.sonicbridge.ui.IpSetupScreen
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

                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                var tvIp by remember {

                    mutableStateOf(
                        Prefs.getTvIp(this)
                    )

                }

                if (tvIp.isBlank()) {

                    IpSetupScreen(

                        onContinue = { ip ->

                            Prefs.saveTvIp(
                                this,
                                ip
                            )

                            tvIp = ip

                        }

                    )

                } else {

                    HomeScreen(

                        uiState = uiState,

                        onConnectClick = {

                            if (uiState.isConnected) {

                                stopReceiver()

                            } else {

                                startReceiver(tvIp)

                            }

                        },

                        onChangeTv = {

                            Prefs.clearTvIp(this)

                            tvIp = ""

                        }

                    )

                }

            }

        }

    }

    private fun startReceiver(ip: String) {

        val intent =
            Intent(
                this,
                AudioPlaybackService::class.java
            ).apply {

                action =
                    AudioPlaybackService.ACTION_CONNECT

                putExtra(
                    AudioPlaybackService.EXTRA_IP,
                    ip
                )

            }

        ContextCompat.startForegroundService(
            this,
            intent
        )

    }

    private fun stopReceiver() {

        val intent =
            Intent(
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