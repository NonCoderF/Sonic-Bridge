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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sparkstudios.sonicbridge.data.Prefs
import com.sparkstudios.sonicbridge.data.PlaybackStateRepository
import com.sparkstudios.sonicbridge.service.AudioPlaybackService
import com.sparkstudios.sonicbridge.ui.HomeScreen
import com.sparkstudios.sonicbridge.ui.AvailableTvsScreen
import com.sparkstudios.sonicbridge.ui.IpSetupScreen
import com.sparkstudios.sonicbridge.ui.MainViewModel
import com.sparkstudios.sonicbridge.ui.TvDiscoveryViewModel
import com.sparkstudios.sonicbridge.ui.theme.SonicBridgeTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val tvDiscoveryViewModel by viewModels<TvDiscoveryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        rpZEAWYtiB6bJ16NuLbGCc6CZ6jJdKfb63()

        setContent {

            SonicBridgeTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0B0F17))
                        .safeDrawingPadding()
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val discoveryState by tvDiscoveryViewModel.uiState.collectAsStateWithLifecycle()

                    var tvIp by remember { mutableStateOf(Prefs.getTvIp(this@MainActivity)) }
                    var showManualSetup by remember { mutableStateOf(false) }
                    var discoveryRestartKey by remember { mutableIntStateOf(0) }

                    LaunchedEffect(tvIp) {
                        if (tvIp.isNotBlank()) {
                            PlaybackStateRepository.updateServerIp(tvIp)
                            if (!uiState.isConnected && !uiState.isConnecting) {
                                startReceiver(tvIp)
                            }
                        }
                    }

                    LaunchedEffect(tvIp, showManualSetup, discoveryRestartKey) {
                        if (tvIp.isBlank() && !showManualSetup) {
                            if (uiState.isConnected || uiState.isConnecting) {
                                stopReceiver()
                                // Let the previous audio WebSocket close before
                                // probing the TV again.
                                delay(750)
                            }
                            tvDiscoveryViewModel.scan()
                        }
                    }

                    if (tvIp.isBlank()) {
                        if (showManualSetup) {
                            IpSetupScreen(onContinue = { ip ->
                                Prefs.selectTv(this@MainActivity, ip)
                                PlaybackStateRepository.updateServerIp(ip)
                                tvIp = ip
                            })
                        } else {
                            AvailableTvsScreen(
                                state = discoveryState,
                                onRefresh = { discoveryRestartKey++ },
                                onSelect = { device ->
                                    Prefs.selectTv(this@MainActivity, device.ip)
                                    PlaybackStateRepository.updateServerIp(device.ip)
                                    tvIp = device.ip
                                },
                                onManualIp = { showManualSetup = true }
                            )
                        }
                    } else {
                        HomeScreen(
                            uiState = uiState,
                            onConnectClick = {
                                if (uiState.isConnected) stopReceiver() else startReceiver(tvIp)
                            },
                            onChangeTv = {
                                Prefs.clearTvIp(this@MainActivity)
                                tvIp = ""
                                showManualSetup = false
                                tvDiscoveryViewModel.scan()
                            }
                        )
                    }
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
