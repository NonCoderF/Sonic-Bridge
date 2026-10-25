package com.sparkstudios.sonicbridge.network

import android.content.Context
import android.net.wifi.WifiManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.delay
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.InetSocketAddress
import java.net.Socket

data class TvDevice(
    val ip: String,
    val name: String = "SonicBridge TV"
)

class TvDiscovery(private val context: Context) {

    private val client = HttpClient(CIO) { install(WebSockets) }

    suspend fun scan(onFound: suspend (TvDevice) -> Unit = {}): List<TvDevice> = withContext(Dispatchers.IO) {
        var localIp: String? = null
        repeat(5) {
            localIp = localIpv4()
            if (localIp != null) return@repeat
            delay(300)
        }
        val resolvedIp = localIp ?: return@withContext emptyList()
        val prefix = resolvedIp.substringBeforeLast('.')
        val candidates = (1..254)
            .map { "$prefix.$it" }
            .filter { it != resolvedIp }

        coroutineScope {
            candidates.chunked(32).flatMap { batch ->
                batch.map { ip ->
                    async {
                        if (isSonicBridgeTv(ip)) {
                            TvDevice(ip).also { onFound(it) }
                        } else null
                    }
                }.awaitAll().filterNotNull()
            }
        }
    }

    private suspend fun isSonicBridgeTv(ip: String): Boolean =
        withTimeoutOrNull(1500) {
            if (!isPortOpen(ip)) return@withTimeoutOrNull false

            var connected = false
            runCatching {
                client.webSocket(host = ip, port = 8080, path = "/audio") {
                    connected = true
                    // Discovery only needs the WebSocket handshake. Do not
                    // wait for the audio socket to close like a real stream.
                    close()
                }
            }
            connected
        } == true

    private fun isPortOpen(ip: String): Boolean = runCatching {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(ip, 8080), 250)
            true
        }
    }.getOrDefault(false)

    private fun localIpv4(): String? {
        val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as? WifiManager
        @Suppress("DEPRECATION")
        val wifiIp = wifiManager?.connectionInfo?.ipAddress ?: 0
        if (wifiIp != 0) {
            return listOf(0, 8, 16, 24).joinToString(".") { shift ->
                ((wifiIp shr shift) and 0xff).toString()
            }
        }

        return NetworkInterface.getNetworkInterfaces().toList()
            .flatMap { it.inetAddresses.toList() }
            .filterIsInstance<Inet4Address>()
            .map { it.hostAddress }
            .firstOrNull { !it.startsWith("127.") }
    }

    fun close() = client.close()
}
