package com.sparkstudios.sonicbridge.ui

data class HomeUiState(
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val serverIp: String = "192.168.1.16",
    val connectionStatus: String = "Disconnected",
    val bytesReceived: Long = 0L,
    val transferRate: String = "0 KB/s",
    val latency: Int = 0,
    val audioFormat: String = "Stereo â€¢ 48 kHz",
    val waveform: List<Float> = List(32) { 0.02f }
)
