package com.sparkstudios.sonicbridge.data

import com.sparkstudios.sonicbridge.ui.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.sqrt

object PlaybackStateRepository {

    private val _state = MutableStateFlow(HomeUiState())
    private var smoothedWaveform = FloatArray(32) { 0.02f }

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
        smoothedWaveform = FloatArray(32) { 0.02f }
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false,
                connectionStatus = "Disconnected",
                bytesReceived = 0,
                transferRate = "0 KB/s",
                latency = 0,
                waveform = List(32) { 0.02f }
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

    private var lastWaveformUpdate = 0L

    fun updateWaveform(pcm: ByteArray) {
        val now = System.currentTimeMillis()
        if (now - lastWaveformUpdate < 20L) return
        lastWaveformUpdate = now

        val bands = FloatArray(32)
        val counts = IntArray(32)
        val frames = pcm.size / 4
        var frame = 0
        var offset = 0
        while (offset + 3 < pcm.size) {
            val left = ((pcm[offset + 1].toInt() shl 8) or (pcm[offset].toInt() and 0xff)).toShort().toInt()
            val right = ((pcm[offset + 3].toInt() shl 8) or (pcm[offset + 2].toInt() and 0xff)).toShort().toInt()
            val band = (frame * 32 / frames.coerceAtLeast(1)).coerceIn(0, 31)
            bands[band] += (left.toFloat() * left + right.toFloat() * right) / 2f
            counts[band]++
            frame++
            offset += 4
        }

        val rawLevels = bands.mapIndexed { index, value ->
                if (counts[index] == 0) 0.02f
                else {
                    val level = sqrt(value / counts[index]) / 32768f
                    (level * 2.4f).coerceIn(0.02f, 1f)
                }
            }

        val smoothed = rawLevels.mapIndexed { index, value ->
                // Fast attack for real audio peaks, slower release for a
                // smooth-looking waveform between packets.
                // Per-bar EMA: almost instant attack for peaks, controlled
                // release so the visualizer stays smooth without feeling slow.
                val coefficient = if (value > smoothedWaveform[index]) 1.0f else 0.18f
                smoothedWaveform[index] =
                    smoothedWaveform[index] * (1f - coefficient) + value * coefficient
                smoothedWaveform[index]
            }

        _state.update { state ->
            state.copy(waveform = smoothed.toList())
        }
    }

    fun updateServerIp(ip: String) {
        _state.update {
            it.copy(serverIp = ip)
        }
    }
}
