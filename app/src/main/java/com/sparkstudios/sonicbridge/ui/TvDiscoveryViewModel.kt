package com.sparkstudios.sonicbridge.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sparkstudios.sonicbridge.network.TvDevice
import com.sparkstudios.sonicbridge.network.TvDiscovery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class TvDiscoveryUiState(
    val isScanning: Boolean = true,
    val devices: List<TvDevice> = emptyList(),
    val error: String? = null
)

class TvDiscoveryViewModel(application: Application) : AndroidViewModel(application) {
    private val discovery = TvDiscovery(application)
    private val _uiState = MutableStateFlow(TvDiscoveryUiState(isScanning = false))
    val uiState: StateFlow<TvDiscoveryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Give Android time to publish the Wi-Fi interface/IP on a cold launch.
            delay(1000)
            while (isActive && _uiState.value.devices.isEmpty()) {
                scan()
                while (isActive && _uiState.value.isScanning) {
                    delay(250)
                }
                if (_uiState.value.devices.isEmpty()) delay(2000)
            }
        }
    }

    fun scan() {
        if (_uiState.value.isScanning) return
        _uiState.value = _uiState.value.copy(isScanning = true, error = null)
        viewModelScope.launch {
            runCatching {
                discovery.scan { device ->
                    _uiState.update { state ->
                        if (state.devices.any { it.ip == device.ip }) state
                        else state.copy(devices = state.devices + device)
                    }
                }
            }
                .onSuccess { _uiState.value = TvDiscoveryUiState(false, it) }
                .onFailure { _uiState.value = TvDiscoveryUiState(false, error = "Could not scan this network") }
        }
    }

    override fun onCleared() { discovery.close() }
}
