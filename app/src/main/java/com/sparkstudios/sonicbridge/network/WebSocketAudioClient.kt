package com.sparkstudios.sonicbridge.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WebSocketAudioClient {

    companion object {
        private const val TAG = "WebSocketAudioClient"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private var socketJob: Job? = null

    private val client = HttpClient(CIO) {

        install(WebSockets)

    }

    var onConnected: (() -> Unit)? = null

    var onDisconnected: (() -> Unit)? = null

    var onAudioReceived: ((ByteArray) -> Unit)? = null

    var onBytesReceived: ((Long) -> Unit)? = null

    var onError: ((Throwable) -> Unit)? = null

    private var totalBytes = 0L

    fun connect(ip: String) {

        if (socketJob != null)
            return

        totalBytes = 0L

        socketJob = scope.launch {

            try {

                client.webSocket(
                    host = ip,
                    port = 8080,
                    path = "/audio"
                ) {

                    Log.i(
                        TAG,
                        "Connected to ws://$ip:8080/audio"
                    )

                    onConnected?.invoke()

                    for (frame in incoming) {

                        if (!isActive)
                            break

                        if (frame is Frame.Binary) {

                            val bytes =
                                frame.readBytes()

                            totalBytes += bytes.size

                            onBytesReceived?.invoke(
                                totalBytes
                            )

                            onAudioReceived?.invoke(
                                bytes
                            )

                        }

                    }

                }

            } catch (e: Exception) {

                Log.e(TAG, "Connection Error", e)

                onError?.invoke(e)

            } finally {

                socketJob = null

                onDisconnected?.invoke()

            }

        }

    }

    fun disconnect() {

        socketJob?.cancel()

        socketJob = null

    }

    fun release() {

        disconnect()

        client.close()

        scope.cancel()

    }

}