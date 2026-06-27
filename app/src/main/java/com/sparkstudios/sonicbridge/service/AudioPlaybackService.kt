package com.sparkstudios.sonicbridge.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sparkstudios.sonicbridge.MainActivity
import com.sparkstudios.sonicbridge.R
import com.sparkstudios.sonicbridge.audio.AudioPlayer
import com.sparkstudios.sonicbridge.data.PlaybackStateRepository
import com.sparkstudios.sonicbridge.network.WebSocketAudioClient
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class AudioPlaybackService : Service() {

    companion object {

        const val CHANNEL_ID = "sonic_bridge"

        const val NOTIFICATION_ID = 1001

        const val ACTION_CONNECT =
            "ACTION_CONNECT"

        const val ACTION_DISCONNECT =
            "ACTION_DISCONNECT"

        const val EXTRA_IP =
            "EXTRA_IP"

    }

    private lateinit var audioPlayer: AudioPlayer

    private lateinit var socketClient: WebSocketAudioClient

    private lateinit var notificationManager: NotificationManagerCompat

    private var serverIp = "192.168.1.16"

    private var bytesReceived = 0L

    override fun onCreate() {

        super.onCreate()

        notificationManager =
            NotificationManagerCompat.from(this)

        createNotificationChannel()

        audioPlayer = AudioPlayer()

        socketClient = WebSocketAudioClient()

    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_CONNECT -> {

                serverIp =
                    intent.getStringExtra(EXTRA_IP)
                        ?: serverIp

                startForeground(
                    NOTIFICATION_ID,
                    createNotification(
                        "Connecting..."
                    )
                )

                connect()

            }

            ACTION_DISCONNECT -> {

                stopStreaming()

                stopSelf()

            }

        }

        return START_STICKY

    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(

                    CHANNEL_ID,

                    "SonicBridge",

                    NotificationManager.IMPORTANCE_LOW

                )

            channel.description =
                "TV Audio Streaming"

            getSystemService(
                NotificationManager::class.java
            ).createNotificationChannel(
                channel
            )

        }

    }

    private fun updateNotification(text: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        notificationManager.notify(
            NOTIFICATION_ID,
            createNotification(text)
        )
    }

    private fun createNotification(
        text: String
    ): Notification {

        val openIntent =
            PendingIntent.getActivity(

                this,

                0,

                Intent(
                    this,
                    MainActivity::class.java
                ),

                PendingIntent.FLAG_IMMUTABLE or
                        PendingIntent.FLAG_UPDATE_CURRENT

            )

        val stopIntent =
            PendingIntent.getService(

                this,

                1,

                Intent(
                    this,
                    AudioPlaybackService::class.java
                ).apply {

                    action = ACTION_DISCONNECT

                },

                PendingIntent.FLAG_IMMUTABLE or
                        PendingIntent.FLAG_UPDATE_CURRENT

            )

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("SonicBridge")
            .setContentText(text)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Disconnect",
                stopIntent
            )
            .build()

    }

    private fun connect() {

        PlaybackStateRepository.setConnecting()

        socketClient.onConnected = {

            audioPlayer.start()

            PlaybackStateRepository.setConnected()

            updateNotification(
                "Receiving TV Audio"
            )

        }

        socketClient.onDisconnected = {

            PlaybackStateRepository.setDisconnected()

            updateNotification(
                "Disconnected"
            )

        }

        socketClient.onBytesReceived = { bytes ->

            bytesReceived = bytes

            PlaybackStateRepository.updateBytes(
                bytes
            )

            PlaybackStateRepository.updateTransferRate(
                formatTransferRate(bytes)
            )

        }

        socketClient.onAudioReceived = { audio ->

            audioPlayer.playAudio(audio)

        }

        socketClient.onError = {

            PlaybackStateRepository.setDisconnected()

            updateNotification(
                "Connection Error"
            )

        }

        socketClient.connect(serverIp)

    }

    private fun stopStreaming() {

        try {

            socketClient.disconnect()

        } catch (_: Exception) {
        }

        try {

            audioPlayer.stop()

        } catch (_: Exception) {
        }

        PlaybackStateRepository.setDisconnected()

    }

    @SuppressLint("DefaultLocale")
    private fun formatBytes(
        bytes: Long
    ): String {

        return when {

            bytes >= 1024L * 1024L ->

                String.format(
                    "%.2f MB",
                    bytes / 1024f / 1024f
                )

            bytes >= 1024L ->

                String.format(
                    "%.2f KB",
                    bytes / 1024f
                )

            else ->

                "$bytes B"

        }

    }

    private var lastBytes = 0L

    private var lastTime =
        System.currentTimeMillis()

    @SuppressLint("DefaultLocale")
    private fun formatTransferRate(
        totalBytes: Long
    ): String {

        val now =
            System.currentTimeMillis()

        val elapsed =
            now - lastTime

        if (elapsed < 1000)
            return "..."

        val diff =
            totalBytes - lastBytes

        val speed =
            diff * 1000L / elapsed

        lastBytes =
            totalBytes

        lastTime =
            now

        return when {

            speed >= 1024 * 1024 ->

                String.format(
                    "%.2f MB/s",
                    speed / 1024f / 1024f
                )

            speed >= 1024 ->

                String.format(
                    "%.2f KB/s",
                    speed / 1024f
                )

            else ->

                "$speed B/s"

        }

    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        stopStreaming()

        stopSelf()

        super.onTaskRemoved(rootIntent)

    }

    private fun releaseResources() {

        try {
            socketClient.disconnect()
        } catch (_: Exception) {
        }

        try {
            audioPlayer.release()
        } catch (_: Exception) {
        }

    }

    override fun onLowMemory() {

        super.onLowMemory()

        System.gc()

    }

    override fun onDestroy() {

        releaseResources()

        notificationManager.cancel(
            NOTIFICATION_ID
        )

        PlaybackStateRepository.setDisconnected()

        super.onDestroy()

    }

}