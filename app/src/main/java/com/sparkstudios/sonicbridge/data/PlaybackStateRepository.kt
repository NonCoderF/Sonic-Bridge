package com.sparkstudios.sonicbridge.data

import com.sparkstudios.sonicbridge.ui.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object PlaybackStateRepository {

    private val _state = MutableStateFlow(HomeUiState())

    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun setConnecting() {
        _state.update {
            it.copy(
                isConnecting = true,
                isConnected = false,
                connectionStatus = "Connecting..."
            )
        }
    }

    fun setConnected() {
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = true,
                connectionStatus = "Connected"
            )
        }
    }

    fun setDisconnected() {
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false,
                connectionStatus = "Disconnected",
                bytesReceived = 0,
                transferRate = "0 KB/s",
                latency = 0
            )
        }
    }

    fun updateBytes(bytes: Long) {
        _state.update {
            it.copy(bytesReceived = bytes)
        }
    }

    fun updateTransferRate(rate: String) {
        _state.update {
            it.copy(transferRate = rate)
        }
    }

    fun updateLatency(latency: Int) {
        _state.update {
            it.copy(latency = latency)
        }
    }

    fun updateServerIp(ip: String) {
        _state.update {
            it.copy(serverIp = ip)
        }
    }
}